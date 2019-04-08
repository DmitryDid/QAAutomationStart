package RestAssured;

import RestAssured.DTO.ResponseObject;
import com.jayway.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * Содержит примеры GET запросов с постепенным усложнением.
 * В примерах данного класса сделан акцент на получение ответа из запроса в разных доступных формах:
 * json, String, Response, собственный объект.
 * Есть пример получения эталонного объекта из файла проекта.
 * Описаны примеры основынх видов проверерок.
 * Задействованы сторонние библиотеки: lombok(требуется подключение библиотеки и установка одноименного плагина), и org.json
 */

public class GET_Examples extends Core {

    @Test
    public void GET_200_easy() {
        given().get("http://s7.addthis.com/l10n/client.ru.min.json");
    }

    @Test
    public void GET_200_getResponse() {
        Response response = given()
                .get("http://s7.addthis.com/l10n/client.ru.min.json")
                .then()
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("language_codes.client"), "ru");
    }

    @Test
    public void GET_200_getResponseAsString() {
        Response response = given()
                .get("http://s7.addthis.com/l10n/client.ru.min.json")
                .then()
                .extract()
                .response();

        String stringResponse = given()
                .get("http://s7.addthis.com/l10n/client.ru.min.json")
                .then()
                .extract()
                .response()
                .asString();

        Assert.assertEquals(response.body().asString(), stringResponse);
    }

    @Test
    public void GET_200_getResponseAsObject() {
        ResponseObject responseObject1 = given()
                .get("https://onesignal.com/api/v1/apps/1bd385ce-bf6b-4d83-be90-84da0bb1de2e/icon")
                .then()
                .extract()
                .response()
                .as(ResponseObject.class);

        ResponseObject responseObject2 = given()
                .get("https://onesignal.com/api/v1/apps/1bd385ce-bf6b-4d83-be90-84da0bb1de2e/icon")
                .then()
                .extract()
                .response()
                .as(ResponseObject.class);

        Assert.assertNotSame(responseObject1, responseObject2);
        Assert.assertEquals(responseObject1, responseObject2);

    }

    @Test
    public void GET_200_getResponseAsJSON() {
        String response = given()
                .get("http://s7.addthis.com/l10n/client.ru.min.json")
                .then()
                .extract()
                .response()
                .asString();

        JSONObject jsonObject = new JSONObject(response);
        JSONArray array = jsonObject.getJSONArray("strings");

        toConsole(array);
    }

    @Test
    public void GET_200_compareWithBenchmark() throws IOException {
        String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/JSON.json")), StandardCharsets.UTF_8);

        String actual = given()
                .get("http://s7.addthis.com/l10n/client.ru.min.json")
                .then()
                .extract()
                .response()
                .asString();

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void GET_200_checkTheResponseCode() {
        given()
                .get("http://s7.addthis.com/l10n/client.ru.min.json")
                .then()
                .statusCode(200);
    }

    @Test
    public void GET_200_checkInBody() {
        given()
                .get("http://s7.addthis.com/l10n/client.ru.min.json")
                .then()
                .statusCode(200)
                .body("language_codes.client", equalTo("ru"))
                .body("top_services.mobile.size()", is("10"));
    }

    @Test
    public void GET_200_checkResponseContainsString() {
        Response response = given()
                .get("http://s7.addthis.com/l10n/client.ru.min.json")
                .then()
                .statusCode(200)
                .extract().response();

        toConsole(response);

        Assert.assertTrue(response.body().asString().contains("\"client\":\"ru\",\"iso\":\"ru\",\"english_name\":\"Russian\""));
    }

    @Test
    public void GET_400_checkResponseMatches() {

        Response response = given()
                .get("https://onesignal.com/api/v1/apps/icon")
                .then()
                .statusCode(400)
                .extract().response();

        toConsole(response);

        Assert.assertTrue(response.body().asString().matches(".*Please include a case-sensitive header of Authorization.*"));
    }
}
