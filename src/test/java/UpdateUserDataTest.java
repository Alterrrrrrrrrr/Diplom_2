import example.org.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;


public class UpdateUserDataTest {
    private UserClient userClient;
    private User currentUser;
    private String accessToken;

    @Before
    public void setUp(){
        userClient = new UserClient();
        currentUser = Generator.createUser();
    }
    @After
    public void tearDown(){
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Изменение имейла пользователя c авторизацией")
    public void editEmailWithAuth(){
        ValidatableResponse response = userClient.userRegister(currentUser);
        accessToken = response.extract().path("accessToken");
        currentUser.setEmail(Generator.generateEmail());
        userClient.editWithAuth(accessToken, currentUser)
                .assertThat()
                .statusCode(200)
                .and()
                .body("user.email",equalTo(currentUser.getEmail()));
    }

    @Test
    @DisplayName("Изменение имени пользователя c авторизацией:")
    public void editNameWithAuth(){
        ValidatableResponse response = userClient.userRegister(currentUser);
        accessToken = response.extract().path("accessToken");
        currentUser.setName(Generator.generateName());
        userClient.editWithAuth(accessToken, currentUser)
                .assertThat()
                .statusCode(200)
                .and()
                .body("user.name",equalTo(currentUser.getName()));
    }

    @Test
    @DisplayName("Изменение имейла пользователя без авторизации")
    public void editEmailNoAuth(){
        ValidatableResponse response = userClient.userRegister(currentUser);
        currentUser.setEmail(Generator.generateEmail());
        userClient.editWithNoAuth(currentUser)
                .assertThat()
                .statusCode(401)
                .and()
                .body("message",equalTo("You should be authorised"));
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    public void editNameNoAuth(){
        ValidatableResponse response = userClient.userRegister(currentUser);
        currentUser.setName(Generator.generateName());
        userClient.editWithNoAuth(currentUser)
                .assertThat()
                .statusCode(401)
                .and()
                .body("message",equalTo("You should be authorised"));
        accessToken = response.extract().path("accessToken");
    }
}