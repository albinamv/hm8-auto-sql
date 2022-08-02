package ru.netology.sql.data;

import lombok.Value;

// хорошая практика — использовать отдельный класс для генерации тестовых данных
public class DataHelper {
    private DataHelper() {
    }

    // вложенный класс
    // для LoginPage
    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    // эти демо данные записываются SUT в БД
    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static AuthInfo getOtherAuthInfo() {
        return new AuthInfo("petya", "123qwerty");
    }

    // вложенный класс
    // для VerificationPage
    @Value
    public static class VerificationCode {
        private String code;
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }

}
