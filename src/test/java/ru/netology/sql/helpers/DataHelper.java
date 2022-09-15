package ru.netology.sql.helpers;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Getter
public class DataHelper {

    // для вставки нового пользователя с уже зашифрованным паролем
    public static String getValidPasswordHash() {
        return new String("$2a$10$du24T/7GER8hkaBWpN2hUeWbu/waOhmnedNewd5n3OxtrqyINzGQK");
    }

    private DataHelper() {
    }

    // вложенный класс
    // для LoginPage
    @Value
    @AllArgsConstructor
    public static class AuthInfo {
        private String id;
        private String login;
        private String password;

        public AuthInfo(String login, String password) {
            this.login = login;
            this.password = password;
            id = null;
        }
    }

    public static AuthInfo generateRegisteredUser() {
        var faker = new Faker();
        return new AuthInfo(faker.random().hex(36), faker.name().username(), "qwerty123");
    }

    public static AuthInfo generateUnregisteredUser() {
        var faker = new Faker();
        return new AuthInfo(faker.name().username(), faker.internet().password());
    }

}
