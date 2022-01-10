package ru.inventorium.qa.tests;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static ru.inventorium.qa.filters.CustomLogFilters.customLogFilter;

public class DemoWebShopTest {
    @BeforeAll
    static void initBaseURIandURL() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
        Configuration.baseUrl = "http://demowebshop.tricentis.com";
        Configuration.remote = "https://user1:1234@selenoid.autotests.cloud/wd/hub/";
    }

    @Test
    @Step("Get Cookie and test profile")
    void getCookieAndTestProfile() {

        String authorizationCookie =
                given()
                        .filter(customLogFilter().withCustomTemplates())
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .formParam("Email", "tester@qa.guru")
                        .formParam("Password", "tester@qa.guru")
                        .when()
                        .post("/login")
                        .then()
                        .statusCode(302)
                        .extract()
                        .cookie("NOPCOMMERCE.AUTH");

        step("Open browser", () ->
                open("/Themes/DefaultClean/Content/images/logo.png"));

        step("Authorize with cookie", () ->
                getWebDriver().manage().addCookie(new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));

        step("Open info page", () ->
                open("/customer/info"));

        step("Assert gender", () ->
                $("#gender-male").shouldBe(checked));

        step("Assert First Name", () ->
                $("#FirstName").shouldHave(value("tester")));

        step("Assert Last Name", () ->
                $("#LastName").shouldHave(value("testeroff")));

        step("Assert Email", () ->
                $("#Email").shouldHave(value("tester@qa.guru")));
    }
}
