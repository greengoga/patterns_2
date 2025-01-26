package ru.netology;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import com.github.javafaker.Faker;


// спецификация нужна для того, чтобы переиспользовать настройки в разных запросах
class AuthTest {

    static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @BeforeAll
    static void setUpAll() {

        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(new AppIbankTest.RegistrationDto("vasya", "password", "active")) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }
}

public class AppIbankTest {

    Faker faker = new Faker();
    String login = faker.name().username();
    String password = faker.internet().password();

    @Data
    @AllArgsConstructor
    public static class RegistrationDto {
        private String login;
        private String password;
        private String status;
    }

    @Test
    void shouldLoginSuccessfully() {
        given()
                .spec(AuthTest.requestSpec)
                .body(new RegistrationDto("vasya", "password", "active"))
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldRefuseLoginIfLoginWrong() {
        given()
                .spec(AuthTest.requestSpec)
                .body(new RegistrationDto("wrong", "password", "active"))
                .when()
                .post("/api/auth")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRefuseLoginIfPassWrong() {
        given()
                .spec(AuthTest.requestSpec)
                .body(new RegistrationDto("vasya", "x", "active"))
                .when()
                .post("/api/auth")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRefuseLoginIfUserBlocked() {
        given()
                .spec(AuthTest.requestSpec)
                .body(new RegistrationDto("vasya", "password", "blocked"))
                .when()
                .post("/api/auth")
                .then()
                .statusCode(400);
    }
}



/*
postman
1. добавление активного пользователя
2. добавление заблокированного пользователя
  логины
1. валидные данные
2. невалидные данные
3. заблокированный пользователь
x ≈ 5
 */

