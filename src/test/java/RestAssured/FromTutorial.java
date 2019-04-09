package RestAssured;

import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.config.HeaderConfig.headerConfig;
import static com.jayway.restassured.matcher.ResponseAwareMatcherComposer.and;
import static com.jayway.restassured.matcher.RestAssuredMatchers.endsWithPath;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

//    ПОЛЕЗНЫЕ ССЫЛКИ:

//    примеры хорошего стиля
//    https://habr.com/ru/post/421005/
//
//    json2POJO
//    http://automation-remarks.com/2017/code-generation/index.html
//
//    очень крутой туториал
//    https://www.baeldung.com/rest-assured-tutorial
//
//    Fiddler пример
//    http://software-testing.ru/library/testing/general-testing/2518-rest-api-testing
//
//    restAssured - это фреймворк для тестирования (Assert не нужен)
//    https://www.journaldev.com/21501/rest-assured-tutorial
//
//    тоже хорошо
//    https://techbeacon.com/app-dev-testing/how-perform-api-testing-rest-assured
//
//    здесь (наверное) можно найти примеры для тестирования API по документации
//    http://appsdeveloperblog.com/rest-assured-tutorial/

public class FromTutorial { // https://github.com/rest-assured/rest-assured/wiki/Usage#response-body

    @Test
    public void anonymousJSONRootValidation() { // Проверка анонимного корня JSON
        //  json: [1, 2, 3]
        when().
                get("/json").
                then().
                body("$", hasItems(1, 2, 3));
    }

    @Test
    public void getListFromResponse_XML() { // получение коллекции из ответа с использованием groovy (работа с XML)
        String response = ""; // настоящий response

        List<String> bookTitles = from(response).getList("store.book.findAll { it.price < 10 }.title");
        List<String> groceries = from(response).getList("shopping.category.find { it.@type == 'groceries' }.item");
        List<String> groceries2 = get("/shopping").path("shopping.category.find { it.@type == 'groceries' }.item");
    }

    @Test
    public void workWithJSON() {
//        {
//            "store":{
//              "book": [
//              {
//                "author":"Nigel Rees",
//                    "category":"reference",
//                    "price":8.95,
//                    "title":"Sayings of the Century"
//              },
//              {
//                "author":"Evelyn Waugh",
//                    "category":"fiction",
//                    "price":12.99,
//                    "title":"Sword of Honour"
//               },
//               {
//                "author":"Herman Melville",
//                    "category":"fiction",
//                    "isbn":"0-553-21311-3",
//                    "price":8.99,
//                    "title":"Moby Dick"
//               },
//               {
//                "author":"J. R. R. Tolkien",
//                    "category":"fiction",
//                    "isbn":"0-395-19395-8",
//                    "price":22.99,
//                    "title":"The Lord of the Rings"
//               }
//            ]
//          }
//        }

        when().
                get("/store").
                then().
                body("store.book.findAll { it.price < 10 }.title", hasItems("Sayings of the Century", "Moby Dick"));

        String response = get("/store").asString();
        List<String> bookTitles = from(response).getList("store.book.findAll { it.price < 10 }.title");

        when().
                get("/store").
                then().
                body("store.book.author.collect { it.length() }.sum()", greaterThan(50));

        when().
                get("/store").
                then().
                body("store.book.author*.length().sum()", greaterThan(50));
    }

    @Test
    public void noteOnSyntax() {
        given().
                param("x", "y").
                expect().
                body("lotto.lottoId", equalTo(5)).
                when().
                get("/lotto");

        given().
                param("x", "y").
                expect().
                statusCode(400).
                body("lotto.lottoId", equalTo(6)).
                when().
                get("/lotto");

        given().
                param("x", "y").
                when().
                get("/lotto").
                then().
                statusCode(400).
                body("lotto.lottoId", equalTo(6));

        // одно и тоже (пример синтаксического сахара)
        given().param("x", "y").and().header("z", "w").when().get("/something").then().assertThat().statusCode(200).and().body("x.y", equalTo("z"));
        // и
        given().
                param("x", "y").
                header("z", "w").
                when().
                get("/something").
                then().
                statusCode(200).
                body("x.y", equalTo("z"));

    }

    @Test
    public void deserializationWithGenerics() { // код в примерезакоментированн, так как требуется имплементация методов анонимного класса
//        [
//          {
//            "id": 2,
//            "name": "An ice sculpture",
//            "price": 12.5,
//            "tags": [
//                "cold",
//                "ice"
//            ],
//            "dimensions": {
//                "length": 7,
//                "width": 12,
//                "height": 9.5
//          },
//            "warehouseLocation": {
//                "latitude": -78.75,
//                "longitude": 20.4
//          }
//        },
//        {
//            "id": 3,
//            "name": "A blue mouse",
//            "price": 25.5,
//            "dimensions": {
//                "length": 3.1,
//                "width": 1,
//                "height": 1
//        },
//            "warehouseLocation": {
//                "latitude": 54.4,
//                "longitude": -32.7
//           }
//         }
//        ]

//        List<Map<String, Object>> products = get("/products").as(new TypeRef<List<Map<String, Object>>>() {
//        });
//
//        assertThat(products, hasSize(2));
//        assertThat(products.get(0).get("id"), equalTo(2));
//        assertThat(products.get(0).get("name"), equalTo("An ice sculpture"));
//        assertThat(products.get(0).get("price"), equalTo(12.5));
//        assertThat(products.get(1).get("id"), equalTo(3));
//        assertThat(products.get(1).get("name"), equalTo("A blue mouse"));
//        assertThat(products.get(1).get("price"), equalTo(25.5));
    }

    @Test
    public void gettingResponseData() {
        InputStream stream = get("/lotto").asInputStream();
        byte[] byteArray = get("/lotto").asByteArray();
        String json = get("/lotto").asString();
    }

    @Test
    public void extractingValuesFromTheResponseAfterValidation() {
//        {
//            "title": "My Title",
//            "_links": {
//            "self": {
//              "href": "/title"
//            },
//            "next": {
//              "href": "/title?page=2"
//            }
//          }
//        }

        // пример 1
        String nextTitleLink =
                given().
                        param("param_name", "param_value").
                        when().
                        get("/title").
                        then().
                        contentType(ContentType.JSON).
                        body("title", equalTo("My Title")).
                        extract().
                        path("_links.next.href");

        get(nextTitleLink);
        // пример 2
        Response response =
                given().
                        param("param_name", "param_value").
                        when().
                        get("/title").
                        then().
                        contentType(ContentType.JSON).
                        body("title", equalTo("My Title")).
                        extract().
                        response();

        String nextTitleLink2 = response.path("_links.next.href");
        String headerValue = response.header("headerName");
    }

    @Test
    public void usingJsonPath() {
        String json = ""; // настоящий json

        int lottoId = from(json).getInt("lotto.lottoId");
        List<Integer> winnerIds = from(json).get("lotto.winners.winnerId");

        JsonPath jsonPath = new JsonPath(json).setRoot("lotto");
        int lottoId2 = jsonPath.getInt("lottoId");
        List<Integer> winnerIds2 = jsonPath.get("winners.winnderId");
    }

    @Test
    public void getHeaders_getCookies_getStatus() {
        Response response = get("/lotto");

        Headers allHeaders = response.getHeaders();
        String headerName = response.getHeader("headerName");

        Map<String, String> allCookies = response.getCookies();
        String cookieValue = response.getCookie("cookieName");

        String statusLine = response.getStatusLine();
        int statusCode = response.getStatusCode();
    }

    @Test
    public void parameters() {
        given().
                param("param1", "value1").
                param("param2", "value2").
                when().
                get("/something");

        given().
                formParam("formParamName", "value1").
                queryParam("queryParamName", "value2").
                when().
                post("/something");

        when().get("/name?firstName=John&lastName=Doe");

        List<String> values = new ArrayList<String>();
        values.add("value1");
        values.add("value2");

        given().param("myList", values);

        given().param("paramName");

        post("/reserve/{hotelId}/{roomNumber}", "My Hotel", 23);

        given().
                pathParam("hotelId", "My Hotel").
                pathParam("roomNumber", 23).
                when().
                post("/reserve/{hotelId}/{roomNumber}");

        given().
                pathParam("hotelId", "My Hotel").
                when().
                post("/reserve/{hotelId}/{roomNumber}", 23);
    }

    @Test
    public void cookies() {
        given().cookie("username", "John").when().get("/cookie").then().body(equalTo("username"));

        given().cookie("cookieName", "value1", "value2");

        Cookie someCookie = new Cookie.Builder("some_cookie", "some_value").setSecured(true).setComment("some comment").build();
        given().cookie(someCookie).when().get("/cookie").then().assertThat().body(equalTo("x"));

        Cookie cookie1 = new Cookie.Builder("username", "John").setComment("comment 1").build();
        Cookie cookie2 = new Cookie.Builder("token", "1234").setComment("comment 2").build();
        Cookies cookies = new Cookies(cookie1, cookie2);
        given().cookies(cookies).when().get("/cookie").then().body(equalTo("username, token"));
    }

    @Test
    public void headers() {
        given().header("MyHeader", "Something");
        given().headers("MyHeader", "Something", "MyOtherHeader", "SomethingElse");

        given().header("headerName", "value1", "value2");

        given().header("x", "1").header("x", "2");

        given().
                config(RestAssuredConfig.config().headerConfig(headerConfig().overwriteHeadersWithName("x"))).
                header("x", "1").
                header("x", "2").
                when().
                get("/something");
    }

    @Test
    public void contentType() {
        given().contentType(ContentType.TEXT);
        given().contentType("application/json");
    }

    @Test
    public void requestBody() {
        given().body("some body"); // Works for POST, PUT and DELETE requests
        given().request().body("some body"); // More explicit (optional)
        given().body(new byte[]{42}); // Works for POST, PUT and DELETE
        given().request().body(new byte[]{42}); // More explicit (optional)
    }

    @Test
    public void verifyingResponseData() {
//        Cookies
        get("/x").then().assertThat().cookie("cookieName", "cookieValue");
        get("/x").then().assertThat().cookies("cookieName1", "cookieValue1", "cookieName2", "cookieValue2");
        get("/x").then().assertThat().cookies("cookieName1", "cookieValue1", "cookieName2", containsString("Value2"));

//        Status
        get("/x").then().assertThat().statusCode(200);
        get("/x").then().assertThat().statusLine("something");
        get("/x").then().assertThat().statusLine(containsString("some"));

//        Headers
        get("/x").then().assertThat().header("headerName", "headerValue");
        get("/x").then().assertThat().headers("headerName1", "headerValue1", "headerName2", "headerValue2");
        get("/x").then().assertThat().headers("headerName1", "headerValue1", "headerName2", containsString("Value2"));

        get("/something").then().assertThat().header("Content-Length", Integer::parseInt, lessThan(1000));

//        Content-Type
        get("/x").then().assertThat().contentType(ContentType.JSON);

//        Full body/content matching
        get("/x").then().assertThat().body(equalTo("something"));


        get("/x").then().body("href", endsWithPath("userId"));
        get("/x").then().body("href", and(startsWith("http:/localhost:8080/"), endsWithPath("userId")));
    }

    @Test
    public void authentication() {
        given().auth().basic("username", "password");
//        RestAssured.authentication = basic("username", "password");
        given().auth().preemptive().basic("username", "password").when().get("/secured/hello").then().statusCode(200);
        given().auth().basic("username", "password").when().get("/secured/hello").then().statusCode(200);
        given().auth().digest("username", "password").when().get("/secured");

//        given().auth().oauth("")
        given().auth().oauth2("accessToken");
        given().auth().preemptive().oauth2("accessToken");
    }
}
