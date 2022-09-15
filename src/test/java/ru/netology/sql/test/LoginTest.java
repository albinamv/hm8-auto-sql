package ru.netology.sql.test;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.netology.sql.helpers.DataHelper;
import ru.netology.sql.helpers.DataHelper.AuthInfo;
import ru.netology.sql.helpers.SQLHelper;
import ru.netology.sql.pages.DashboardPage;
import ru.netology.sql.pages.LoginPage;

import java.sql.DriverManager;

import static com.codeborne.selenide.Selenide.open;

class LoginTest {
    DashboardPage dashboardPage;
    LoginPage loginPage;

    static AuthInfo registeredUser;
    static AuthInfo unregisteredUser;
    static AuthInfo registeredUserWithWrongPW;

    static String urlDB = "jdbc:mysql://localhost:3306/app";
    static String userDB = "app";
    static String passwordDB = "pass";

    @BeforeAll
    @SneakyThrows
    static void setUp() {
        // инициализация данных пользователей для тестов
        registeredUser = DataHelper.generateRegisteredUser();
        unregisteredUser = DataHelper.generateUnregisteredUser();
        registeredUserWithWrongPW = new DataHelper.AuthInfo(registeredUser.getLogin(), "Wrong_Password0");

        // добавляем в БД нового рандомного пользователя для проверки авторизации
        SQLHelper.insertUser(registeredUser.getId(), registeredUser.getLogin(), DataHelper.getValidPasswordHash());

    }

    @BeforeEach
    void openPage() {
        open("http://localhost:9999");
        loginPage = new LoginPage(); // создаём новый PO со страницей авторизации
    }

    @Test
    @SneakyThrows
    void shouldLogin() {
        var verificationPage = loginPage.validLogin(registeredUser); // логинимся

        // получаем последний код для пользователя с нужным логином
        // вводим его в VerificationPage
        // и "пытаемся перейти" на DashboardPage
        dashboardPage = verificationPage.validVerify(SQLHelper.getValidAuthCode(registeredUser.getLogin()));

    }

    @Test
    void shouldNotLogin() {
        loginPage.invalidLogin(unregisteredUser);
    }

    @Test
    void shouldBeBlockedAfter3InvalidLogins() {
        loginPage.invalidLogin(registeredUserWithWrongPW);
        loginPage.clearTheForm();
        loginPage.invalidLogin(registeredUserWithWrongPW);
        loginPage.clearTheForm();
        loginPage.invalidLogin(registeredUserWithWrongPW);

        loginPage.isTheFormBlocked();
    }

    @Test
    @SneakyThrows
    void shouldNotVerifyWithExpiredCode() {
        var verificationPage = loginPage.validLogin(registeredUser);

        // открываем страницу для авторизации заново
        open("http://localhost:9999");
        var newLoginPage = new LoginPage();
        var newVerificationPage = newLoginPage.validLogin(registeredUser);

        // получаем первый (недействительный) код для пользователя с нужным логином
        // и вводим его на новой VerificationPage
        newVerificationPage.invalidVerify(SQLHelper.getExpiredAuthCode(registeredUser.getLogin()));
    }

    @AfterAll
    @SneakyThrows
    static void cleanDB() {
        SQLHelper.cleanDatabase();
    }

}

