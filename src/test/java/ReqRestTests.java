import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class ReqRestTests {

    @BeforeAll

    public static void setup(){

    RestAssured.baseURI = "https://reqres.in";
    RestAssured.basePath = "/api";
    RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

    requestSpecification = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .build();
}


    @Test

    //metodo Get
    public void logRTest() {

                given()
               .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
               .post("login")
               .then()
               .statusCode(HttpStatus.SC_OK)
               .body("token", notNullValue());
    }

    @Test
    //Metodo Post
    public void getSingleUserTest() {

                given()
                .get("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("data.id", equalTo(2));

    }

    @Test
    //Metodo Delete
    public void deleteUserTest() {

                given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

    }

    @Test
    //Metodo Patch update
    public void patchUpdateUserTest() {

                String nameUpdate =  given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath()
                .getString("name");
                 assertThat(nameUpdate, equalTo("morpheus"));
    }

    @Test
    //Metodo put
    public void putUserTest(){
                String jobUpdate =  given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .put("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath()
                .getString("job");
                 assertThat(jobUpdate, equalTo("zion resident"));

    }

    @Test

    public void getAllUsersTest(){

        Response respose = given()
                .get("users?page=2");


        Headers headers = respose.getHeaders();
        int statusCode = respose.getStatusCode();
        String body = respose.getBody().asString();
        String contentType = respose.getContentType();

        assertThat(statusCode,equalTo(HttpStatus.SC_OK));
        System.out.println("body:" + body);
        System.out.println("content Type: " + contentType);
        System.out.println("Headers: "+ headers.toString());
        System.out.println("***************************************************");
        System.out.println("***************************************************");
        System.out.println(headers.get("Content-Type"));
        System.out.println(headers.get("Transfer-Encoding"));
    }

    @Test
//traes todos los usuarios desde el 10
    public void getAllUsersTest2(){
String response =
         given()
        .when()
        .get("users?page=2")
        .then()
        .extract()
        .body()
        .asString();
    int page = from(response).get("page");
    int total_pages = from(response).get("total_pages");
    int idFirstUser = from(response).get("data[0].id");

        System.out.println("page: " + page);
        System.out.println("total_pages: " + total_pages);
        System.out.println("id first user: " + idFirstUser);

        List<Map> usersWithIdGreaterTha10 = from(response).get("data.findAll { user -> user.id > 10 }");
        String email = usersWithIdGreaterTha10.get(0).get("email").toString();

        List<Map> user = from(response).get("data.findAll { user -> user.id > 10 && user.last_name == 'Howell'}");
        int id = Integer.valueOf(user.get(0).get("id").toString());
    }


    @Test

    public void createUserTest(){

        String response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"leader\"\n" +
                        "}")
                .post("users")
                .then()
                .extract()
                .body()
                .asString();

User user = from(response).getObject("", User.class);
        System.out.println(user.getId());
        System.out.println(user.getJob());



    }

    }