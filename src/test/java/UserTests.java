import example.org.UserClient;
import example.org.Generator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import example.org.User;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserTests {
    private User currentUser;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp(){
        userClient = new UserClient();
        currentUser = Generator.createUser();
    }
    @After
    public void tearDown(){
        ValidatableResponse response = userClient.userBasicAuth(currentUser);
        accessToken = response.extract().path("accessToken");
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создать уникального пользователя")
    public void createNewUser(){
        ValidatableResponse response = userClient.userRegister(currentUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken",notNullValue());
        accessToken = response.extract().path("accessToken");

    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    public void createExistingUser(){
        userClient.userRegister(currentUser);
        User secondUser = currentUser;
        userClient.userRegister(secondUser)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("message",equalTo("User already exists"));
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void loginUser(){
        ValidatableResponse response = userClient.userRegister(currentUser);
        userClient.userBasicAuth(currentUser)
                .assertThat()
                .statusCode(200)
                .and()
                .body("user.email",equalTo(currentUser.getEmail()));
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void loginUserFails(){
        ValidatableResponse response = userClient.userRegister(currentUser);
        userClient.userBasicAuth(new User(currentUser.getEmail(),"123321"))
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message",equalTo("email or password are incorrect"));
        accessToken = response.extract().path("accessToken");
    }
}