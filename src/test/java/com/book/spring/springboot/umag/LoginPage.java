package com.book.spring.springboot.umag;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class LoginPage {
    private final SelenideElement loginField = $x("//input[@type='text']");
    private final SelenideElement passwordField = $x("//input[@type='password']");
    private final SelenideElement submitButton = $x("//button[@type='submit']");

    public LoginPage() {
        Selenide.open("/auth/login");
    }

    public void setPhone(String phone, String pwd) {
        loginField.setValue(phone);
        passwordField.setValue(pwd);
        submitButton.click();

    }

}
