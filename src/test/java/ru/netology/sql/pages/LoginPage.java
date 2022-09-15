package ru.netology.sql.pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.sql.helpers.DataHelper;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private SelenideElement loginField = $("[data-test-id=login] input");
    private SelenideElement passwordField = $("[data-test-id=password] input");
    private SelenideElement loginButton = $("[data-test-id=action-login]");
    private SelenideElement error = $("[data-test-id = error-notification]");

    // авторизация, возвращает страницу с кодом подтверждения
    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        fillLoginForm(info);
        return new VerificationPage();
    }

    // авторизация с неверными данными, должна быть видна ошибка
    public void invalidLogin(DataHelper.AuthInfo info) {
        fillLoginForm(info);
        error.shouldBe(visible).shouldHave(text("Неверно указан логин или пароль"));
    }

    // метод заполнения и отправки формы
    void fillLoginForm(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
    }

    // метод очистки формы
    public void clearTheForm() {
        loginField.sendKeys(Keys.CONTROL + "A");
        loginField.sendKeys(Keys.BACK_SPACE);

        passwordField.sendKeys(Keys.CONTROL + "A");
        passwordField.sendKeys(Keys.BACK_SPACE);
    }

    // проверка блокировки формы
    public void isTheFormBlocked() {
        loginButton.shouldBe(disabled);
    }
}
