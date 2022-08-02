package ru.netology.sql.test;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestInstance;
import ru.netology.sql.data.DataHelper;
import ru.netology.sql.page.DashboardPage;
import ru.netology.sql.page.LoginPage;

import java.sql.DriverManager;

import static com.codeborne.selenide.Selenide.open;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginTest {
    DashboardPage dashboardPage;
    LoginPage loginPage;

    @BeforeAll
    void login() {
        open("http://localhost:9999");
        loginPage = new LoginPage(); // создаём новый PO со страницей авторизации

   }

    @Test
    @SneakyThrows
    void shouldLoginDBUtils() {
        var authInfo = DataHelper.getOtherAuthInfo(); // получаем данные подготовленного пользователя
        var verificationPage = loginPage.validLogin(authInfo); // логинимся
        var authCodeSQL = "SELECT code FROM auth_codes WHERE user_id = (SELECT id FROM users WHERE login = ?) ORDER BY created DESC LIMIT 1;";
        var runner = new QueryRunner();

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            String code = runner.query(conn, authCodeSQL, new ScalarHandler<>(), authInfo.getLogin());
            dashboardPage = verificationPage.validVerify(code);
        }
    }

    @AfterAll
    @SneakyThrows
    void cleanDB() {
        var runner = new QueryRunner();
        var clearUsersSQL = "DELETE FROM users;";
        var clearCardsSQL = "DELETE FROM cards;";
        var clearCodesSQL = "DELETE FROM auth_codes;";
        var clearTransactionsSQL = "DELETE FROM card_transactions;";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );

        ) {
            runner.execute(conn, clearCodesSQL);
            runner.execute(conn, clearTransactionsSQL);
            runner.execute(conn, clearCardsSQL);
            runner.execute(conn, clearUsersSQL);
        }
    }

}

