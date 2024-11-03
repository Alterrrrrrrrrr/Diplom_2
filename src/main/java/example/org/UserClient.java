package example.org;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient{
    private static final String Register_URL = BASE_URI + "auth/register";
    private static final String Auth_URL = BASE_URI + "auth/login";
    private static final String User_URL = BASE_URI + "auth/user";

    @Step("Создание пользователя")
    public ValidatableResponse userRegister(User user){
        return given()
                .spec(getReqSpec())
                .body(user)
                .when()
                .post(Register_URL)
                .then().log().all();
    }
    @Step("Авторизация по логину и паролю")
    public ValidatableResponse userBasicAuth(User user){
        return given()
                .spec(getReqSpec())
                .body(new User(user.getEmail(),user.getPassword()))
                .when()
                .post(Auth_URL)
                .then().log().all();
    }

    @Step("Редактирование данных с авторизацией")
    public ValidatableResponse editWithAuth(String accessToken,User user){
        return  given()
                .spec(getReqSpec())
                .header("Authorization",accessToken)
                .body(new User(user.getEmail(),user.getPassword(),user.getName()))
                .when()
                .patch(User_URL)
                .then().log().all();
    }

    @Step("Редактирование данных без авторизацией")
    public ValidatableResponse editWithNoAuth(User user){
        return  given()
                .spec(getReqSpec())
                .body(user)
                .when()
                .patch(User_URL)
                .then().log().all();
    }

    @Step("Удаление")
    public ValidatableResponse deleteUser(String accessToken){
        return given()
                .spec(getReqSpec())
                .auth().oauth2(accessToken)
                .when()
                .delete(User_URL)
                .then().log().all();
    }
}