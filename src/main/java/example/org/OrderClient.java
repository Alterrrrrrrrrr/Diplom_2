package example.org;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import io.qameta.allure.Step;
import java.util.*;

public class OrderClient extends RestClient{
    private static final String ORDER = BASE_URI + "orders";
    private Response orderResponse;
    private List<String> currentOrder = new ArrayList<>();




    @Step("Извлечение ответа")
    public Response getOrderResponse() {
        return orderResponse;
    }

    @Step("Получение списка ингредиентов")
    public void setIngredientsList() {
        currentOrder = given()
                .spec(getReqSpec())
                .get("ingredients")
                .then().log().all()
                .extract()
                .path("data._id");
    }

    @Step("Создание заказа с авторизацией")
    public void createOrderAuth(String accessToken) {
        Random random = new Random();
        String randomIngredientFromList = currentOrder.get(random.nextInt(currentOrder.size()));
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("ingredients", randomIngredientFromList);
        orderResponse = given()
                .spec(getReqSpec())
                .header("Authorization",accessToken)
                .body(dataMap)
                .when().log().all()
                .post("orders");
    }

    @Step("Создание заказа без авторизации")
    public void createOrderNoAuth() {
        Random random = new Random();
        String randomIngredientFromList = currentOrder.get(random.nextInt(currentOrder.size()));
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("ingredients", randomIngredientFromList);
        orderResponse = given()
                .spec(getReqSpec())
                .body(dataMap)
                .when().log().all()
                .post("orders");
    }

    @Step("Создание заказа с ингридиентами")
    public void createOrderIngredients(String accessToken) {
        Random random = new Random();
        String randomIngredientFromList = currentOrder.get(random.nextInt(currentOrder.size()));
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("ingredients", randomIngredientFromList);
        orderResponse = given()
                .spec(getReqSpec())
                .header("Authorization",accessToken)
                .body(dataMap)
                .when().log().all()
                .post("orders");
    }

    @Step("Создание заказа без ингридиентов")
    public void createOrderNoIngredients() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("ingredients", null);
        orderResponse = given()
                .spec(getReqSpec())
                .body(dataMap)
                .when().log().all()
                .post("orders");
    }

    @Step("Создание заказа c неверным хэшэм ингредиента")
    public void createOrderWithInvalidHash() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("ingredients", "null");
        orderResponse = given()
                .spec(getReqSpec())
                .body(dataMap)
                .when().log().all()
                .post("orders");
    }

    @Step("Получение заказов пользователя")
    public void getUserOrders(String accessToken){
        orderResponse = given()
                .spec(getReqSpec())
                .header("Authorization",accessToken)
                .when().log().all()
                .get(ORDER);
    }

}