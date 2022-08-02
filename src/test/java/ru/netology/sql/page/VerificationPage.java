package ru.netology.sql.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.sql.data.DataHelper;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
    private SelenideElement codeField = $("[data-test-id=code] input");
    private SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private SelenideElement error = $("[data-test-id = error-notification]");

    public VerificationPage() {
        // при создании объекта вызывается проверка - есть ли поле для кода
        codeField.shouldBe(visible);
    }

    public DashboardPage validVerify(String code) {
        codeField.setValue(code);
        verifyButton.click();
        return new DashboardPage();
    }

    public void invalidVerify(String code) {
        codeField.setValue(code);
        verifyButton.click();
        error.shouldBe(visible).shouldHave(text("Неверно указан код"));
    }
}
