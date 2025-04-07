package stepdefinitions;

import io.cucumber.java.AfterAll;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;


public class Hooks {

    public static List<String> allObjectIds = new ArrayList<>();

    private static Response response;

    @AfterAll
    public static void clearObjectIds() {

        //Clear all the object ids created as part of automation pack
        System.out.println("After All Scenarios: Total number of ObjectIds used in the execution and not deleted - " + allObjectIds.size());
        allObjectIds.forEach(it -> {
            response = RestAssured.given()
                    .delete(Apisteps.baseUrl.concat("/").concat(it));
            System.out.println("Tried to delete id - " + it + " and response is \n" + response.getBody().asString());
        });
    }
}
