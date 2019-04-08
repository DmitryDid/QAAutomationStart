package RestAssured;


import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

import static com.jayway.restassured.RestAssured.*;

public class Core {

    String username;
    String password;
    Cookies baseCookies;

    String METHOD = "/platform2mca/rest/api/v1/method";
    String PARAMS = "/param-storage/api/1/params";
    String VIEW = "/platform2mca/rest/api/v1/view";
    String MODEL = "/platform2mca/rest/api/v1/model";
    String AUTH = "/platform2mca/rest/auth/v1/session";

    String PREPARE = "prepare";
    String VALIDATE = "validate";
    String EXECUTE = "execute";
    String CANCEL = "cancel";


    @BeforeClass
    void setUp() {
        defaultParser = Parser.JSON;
        username = "AVTO_ROOT";
        password = "AVTO_ROOT";
    }

    private void getBasicAuthentication() {
        baseCookies = given()
                .spec(getAuthSpecification())
                .param("username", username)
                .param("password", password)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response().getDetailedCookies();
    }

    protected RequestSpecification getRequestSpecification(String className, String methodName) {
        return new RequestSpecBuilder()
                .setBaseUri(DEFAULT_URI)
                .setPort(port)
                .setBasePath(String.format("%s/%s/%s/", METHOD, className, methodName))
                .setContentType(ContentType.JSON)
                .build();
    }

    protected RequestSpecification getAuthSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(DEFAULT_URI)
                .setPort(port)
                .setBasePath(AUTH)
                .build();
    }

    protected RequestSpecification getStorageSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(DEFAULT_URI)
                .setPort(port)
                .setContentType(ContentType.JSON)
                .setBasePath(PARAMS)
                .build();
    }

    protected RequestSpecification getPlayerSpecification() {
        return getStorageSpecification();
    }

    protected RequestSpecification getViewSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(DEFAULT_URI)
                .setPort(port)
                .setBasePath(VIEW)
                .setContentType(ContentType.JSON)
                .build();
    }

    protected RequestSpecification getModelSpecification(String classId) {
        return new RequestSpecBuilder()
                .setBaseUri(DEFAULT_URI)
                .setPort(port)
                .setBasePath(String.format("%s/%s/views", MODEL, classId))
                .setContentType(ContentType.JSON)
                .build();
    }

    protected RequestSpecification getMethodSpecification(String className) {
        return new RequestSpecBuilder()
                .setBaseUri(DEFAULT_URI)
                .setPort(port)
                .setBasePath(String.format("%s/%s/methods", MODEL, className))
                .setContentType(ContentType.JSON)
                .build();
    }

    protected String getUniqueId() {
        String unique = String.valueOf(System.currentTimeMillis());
        return "338c6664-e2bc-4733-b888-5e9558c" + unique.substring(unique.length() - 5);
    }

    protected String getBearerToken(String username, String password) {
        Response response = given()
                .spec(getAuthSpecification())
                .param("username", username)
                .param("password", password)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("Выполнена Bearer/Basic authentication: " + response.body().asString());
        return response.jsonPath().getString("token");
    }

    protected void unlockInstance(String className, String shortName, String objectId, String frameId) {
        System.out.println("\n----------------------- снятие блокировки с экземпляра (базовый пользователь)-----------------------------");
        given()
                .log().all()
                .auth().preemptive().basic(username, password)
                .cookies(baseCookies)
                .spec(getRequestSpecification(className, shortName))
                .body("{\"frameId\":\"" + frameId + "\",\"objectId\":\"" + objectId + "\"}")
                .when()
                .put(CANCEL)
                .then()
                .statusCode(204);
    }

    protected void forceUnlock(String className, String shortName, String objectId, int maxFrameId) {
        for (int i = 0; i < maxFrameId; i++) {
            given()
                    .auth().preemptive().basic(username, password)
                    .cookies(baseCookies)
                    .spec(getRequestSpecification(className, shortName))
                    .body("{\"frameId\":\"" + i + "\",\"objectId\":\"" + objectId + "\"}")
                    .when()
                    .put(CANCEL);
        }
    }

    protected void toConsole(Object obj) {
        System.out.print("\ntoConsole():");
        if (obj instanceof Response) {
            Response response = (Response) obj;
            System.out.println("\n" + response.body().asString() + "\n");
            return;
        }
        System.out.println("\n" + obj + "\n");
    }
}