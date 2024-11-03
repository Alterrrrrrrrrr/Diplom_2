import example.org.OrderClient;
import example.org.UserClient;
import example.org.Generator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import example.org.User;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private User currentUser;
    private String accessToken;

    @Before
    public void setUp(){
        userClient = new UserClient();
        orderClient = new OrderClient();
        currentUser = Generator.createUser();
    }

    @After
    public void tearDown(){
        ValidatableResponse response = userClient.userBasicAuth(currentUser);
        accessToken = response.extract().path("accessToken");
        if (accessToken == null)
            return;
        else userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void verifyCreateOrderAuth(){
        ValidatableResponse response = userClient.userRegister(currentUser);
        accessToken = response.extract().path("accessToken");
        orderClient.setIngredientsList();
        orderClient.createOrderAuth(accessToken);
        orderClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("order.number",notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void verifyCreateOrderNoAuth() {
        orderClient.setIngredientsList();
        orderClient.createOrderNoAuth();
        orderClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .and()
                .body("order.number",notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингридиентами")
    public void verifyCreateOrderIngredients() {
        ValidatableResponse response = userClient.userRegister(currentUser);
        accessToken = response.extract().path("accessToken");
        orderClient.setIngredientsList();
        orderClient.createOrderIngredients(accessToken);
        orderClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .and()
                .body("order.number",notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингридиентов")
    public void verifyCreateOrderNoIngredients(){
        orderClient.createOrderNoIngredients();
        orderClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента")
    public void verifyCreateOrderWithInvalidHash(){
        orderClient.createOrderWithInvalidHash();
        orderClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    public void verifyGetUserOrders(){
        ValidatableResponse response = userClient.userRegister(currentUser);
        accessToken = response.extract().path("accessToken");
        orderClient.getUserOrders(accessToken);
        orderClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success",equalTo(true))
                .and()
                .body("total",notNullValue());

    }

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    public void verifyGetUserOrdersNoAuth(){
        orderClient.getUserOrders("");
        orderClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message",equalTo("You should be authorised"));
    }
}