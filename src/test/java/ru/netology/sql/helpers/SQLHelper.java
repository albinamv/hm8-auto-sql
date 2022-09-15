package ru.netology.sql.helpers;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;

@Getter
public class SQLHelper {

    private SQLHelper() {
    }

    static String urlDB = "jdbc:mysql://localhost:3306/app";
    static String userDB = "app";
    static String passwordDB = "pass";
    static QueryRunner runner = new QueryRunner();

    @SneakyThrows
    public static void insertUser(String id, String login, String password) {
        var insertUserSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";

        try (
            var conn = DriverManager.getConnection(urlDB, userDB, passwordDB);
        ) {
            runner.insert(conn, insertUserSQL, new ScalarHandler<>(), id, login, password);
        }
    }

    @SneakyThrows
    public static String getValidAuthCode(String login) {
        var authCodeSQL = "SELECT code FROM auth_codes WHERE user_id = (SELECT id FROM users WHERE login = ?) ORDER BY created DESC LIMIT 1;";
        String code;

        try (
            var conn = DriverManager.getConnection(urlDB, userDB, passwordDB);
        ) {
            code = runner.query(conn, authCodeSQL, new ScalarHandler<>(), login);
        }
        return code;
    }

    @SneakyThrows
    public static String getExpiredAuthCode(String login) {
        var authCodeSQL = "SELECT code FROM auth_codes WHERE user_id = (SELECT id FROM users WHERE login = ?) ORDER BY created ASC LIMIT 1;";
        String code;

        try (
            var conn = DriverManager.getConnection(urlDB, userDB, passwordDB);
        ) {
            code = runner.query(conn, authCodeSQL, new ScalarHandler<>(), login);
        }
        return code;
    }

    @SneakyThrows
    public static void cleanDatabase() {
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
