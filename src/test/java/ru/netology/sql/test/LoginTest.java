package ru.netology.sql.test;

import lombok.SneakyThrows;
import lombok.val;
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
    void shouldLogin() {
        var authInfo = DataHelper.getAuthInfo(); // получаем данные подготовленного пользователя
        var verificationPage = loginPage.validLogin(authInfo); // логинимся
        var authCodeSQL = "SELECT code FROM auth_codes WHERE user_id = (SELECT id FROM users WHERE login = ?) ORDER BY created DESC LIMIT 1;";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
                var codeStmt = conn.prepareStatement(authCodeSQL);
        ) {
            codeStmt.setString(1, "vasya");
            try (var rs = codeStmt.executeQuery()) {
                if (rs.next()) {
                    // выборка значения по индексу столбца (нумерация с 1)
                    String authCode = rs.getString("code");
                    dashboardPage = verificationPage.validVerify(authCode); // вводим код и попадаем на страницу с картами
                }
            }
        }
    }

}

