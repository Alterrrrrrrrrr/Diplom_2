import example.org.UserClient;

import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import example.org.User;
import static org.hamcrest.Matchers.equalTo;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;

@RunWith(Parameterized.class)
public class CreateUserWithoutDataTest {
    private UserClient userClient;
    private User user;

    static Faker faker = new Faker();

    public CreateUserWithoutDataTest(User user){
        this.user = user;
    }
    @Parameterized.Parameters
    public static Object[][]getData(){
        return new Object[][]{
                {new User(null,faker.internet().password(),faker.name().firstName())},
                {new User(faker.internet().emailAddress(),null,faker.name().firstName())},
                {new User(faker.internet().emailAddress(),faker.internet().password(),null)}
        };
    }

    @Before
    public void setUp(){
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей")
    public void checkUserFailedCreation(){
        userClient.userRegister(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message",equalTo("Email, password and name are required fields"));
    }
}