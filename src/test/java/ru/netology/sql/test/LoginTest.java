package ru.netology.sql.test;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.netology.sql.data.DataHelper;
import ru.netology.sql.data.DataHelper.AuthInfo;
import ru.netology.sql.page.DashboardPage;
import ru.netology.sql.page.LoginPage;

import java.sql.DriverManager;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.switchTo;

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
        var runner = new QueryRunner();
        var dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";

        try (
            var conn = DriverManager.getConnection(urlDB, userDB, passwordDB);
        ) {
            runner.update(conn, dataSQL, registeredUser.getId(), registeredUser.getLogin(), DataHelper.getValidPasswordHash());
        }
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
        var authCodeSQL = "SELECT code FROM auth_codes WHERE user_id = (SELECT id FROM users WHERE login = ?) ORDER BY created DESC LIMIT 1;";
        var runner = new QueryRunner();

        try (
            var conn = DriverManager.getConnection(urlDB, userDB, passwordDB);
        ) {
            String code = runner.query(conn, authCodeSQL, new ScalarHandler<>(), registeredUser.getLogin());
            dashboardPage = verificationPage.validVerify(code);
        }
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
        var verificationPage = loginPage.validLogin(registeredUser);ge

        // открываем страницу для авторизации заново
        open("http://localhost:9999");
        var newLoginPage = new LoginPage();
        var newVerificationPage = newLoginPage.validLogin(registeredUser);

        // получаем первый код для пользователя с нужным логином
        var authCodeSQL = "SELECT code FROM auth_codes WHERE user_id = (SELECT id FROM users WHERE login = ?) ORDER BY created ASC LIMIT 1;";
        var runner = new QueryRunner();

        try (
            var conn = DriverManager.getConnection(urlDB, userDB, passwordDB);
        ) {
            String code = runner.query(conn, authCodeSQL, new ScalarHandler<>(), registeredUser.getLogin());
            newVerificationPage.invalidVerify(code);
        }
    }

    @AfterAll
    @SneakyThrows
    static void cleanDB() {
        var runner = new QueryRunner();
        var clearUsersSQL = "DELETE FROM users;";
        var clearCardsSQL = "DELETE FROM cards;";
        var clearCodesSQL = "DELETE FROM auth_codes;";
        var clearTransactionsSQL = "DELETE FROM card_transactions;";

        try (
            var conn = DriverManager.getConnection(urlDB, userDB, passwordDB);
        ) {
            // очистка таблиц в нужном порядке
            runner.execute(conn, clearCodesSQL);
            runner.execute(conn, clearTransactionsSQL);
            runner.execute(conn, clearCardsSQL);
            runner.execute(conn, clearUsersSQL);
        }
    }

}

