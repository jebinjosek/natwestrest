package stepdefinitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class Apisteps {

    //Static Values
    private static final String SOURCE_CONFIG_PATH = "src/test/resources/config/WebService.json";
    private static final String SOURCE_REQUEST_PATH = "src/test/resources/payload/request/";
    private static final String SOURCE_RESPONSE_PATH = "src/test/resources/payload/response/";
    private static File sourceConfig = new File(SOURCE_CONFIG_PATH);
    public static String baseUrl = JsonPath.from(sourceConfig).getString("BASE_URL");


    private Response response;
    private String createdItemId;
    private String requestBody = "";
    private List<String> objectIds = new ArrayList<>();

    private Map<String, Object> jsonMapReq;
    private List<Map<String, Object>> listOfJsonMapReq = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    ;


    @Given("an object ,{string} is created")
    public void an_item_is_created(String dataPath) throws IOException {

        // Get the JSON file, deserialize and save in a map.Will be useful in dependent steps
        String path = SOURCE_REQUEST_PATH.concat(dataPath);
        String jsonData = new String(Files.readAllBytes(Paths.get(path)));
        jsonMapReq = objectMapper.readValue(jsonData, Map.class);
        listOfJsonMapReq.add(jsonMapReq);

        requestBody = objectMapper.writeValueAsString(jsonMapReq);
    }

    @When("the request to add the item is made")
    public void the_request_to_add_the_item_is_made() {

        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(baseUrl);
        System.out.println("Response is " + response.prettyPrint());

        //Store the generated Id's for dependent steps execution
        createdItemId = response.jsonPath().getString("id");
        objectIds.add(createdItemId);
        //Store createdId to hold till end of execution of all scenarios
        if (createdItemId != null)
            Hooks.allObjectIds.add(createdItemId);
        System.out.println(response.prettyPrint());
    }

    @Then("a {int} response code is returned")
    public void a_response_code_is_returned(int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }


    @Then("object is added")
    public void object_is_added() throws JsonProcessingException {

        JsonPath jsonPathRes = response.jsonPath();
        JsonPath jsonPathReq = new JsonPath(objectMapper.writeValueAsString(jsonMapReq));
        verifyPostObjectResponseBody(jsonPathReq, jsonPathRes);
    }

    @Then("object is not added")
    public void object_is_not_added() {
        String expectedIncorrectBodyJson = "{\n" +
                "    \"error\": \"400 Bad Request. If you are trying to create or update the data, potential issue is that you are sending incorrect body json or it is missing at all.\"\n" +
                "}";
        response.then()
                .body(equalTo(expectedIncorrectBodyJson));
        assertEquals("400 Bad request body as not expected", expectedIncorrectBodyJson, response.getBody().asString());

    }

    @When("^I get the item by its Id(?: \"([^\"]+)\")?$")
    public void i_get_the_item_by_its_id(String id) {

        response = RestAssured.given()
                .get(baseUrl.concat("/").concat(id != null ? id : objectIds.getFirst()));
        System.out.println("GET Response is " + response.prettyPrint());
    }

    @When("^I get the item by its id list?$")
    public void i_get_the_item_by_its_id_list() {

        response = RestAssured.given()
                .queryParams("id", objectIds)
                .get(baseUrl);
        System.out.println("GET Response is " + response.prettyPrint());
    }


    @Then("^received object not found message for id(?: \"([^\"]+)\")?$")
    public void received_object_not_found_message_for_id(String id) throws JsonProcessingException {
        // Extract the Id if its passed from feature else pick the first in the saved list
        String extractedID = (id != null) ? id : objectIds.getFirst();
        String expectedBody = "{\n" +
                "    \"error\": \"Oject with id=" + extractedID + " was not found.\"\n" +
                "}";
        assertEquals("Object not found message not matching", expectedBody, response.getBody().asString());
    }

    @Then("the response matched with the id")
    public void the_response_matched_with_the_id() throws JsonProcessingException {

        JsonPath jsonPathRes = response.jsonPath();
        JsonPath jsonPathReq = new JsonPath(objectMapper.writeValueAsString(jsonMapReq));
        verifyGetObjectResponseBody(jsonPathReq, jsonPathRes, createdItemId);
    }


    @Then("the response returns a list of items")
    public void the_response_returns_a_list_of_items() throws JsonProcessingException {

        assertEquals(objectIds.size(), response.jsonPath().getList("$").size());

        //Convert Response as JsonPathList
        List<JsonPath> jsonPathListResp = new ArrayList<>();
        List<Map<String, Object>> items = response.jsonPath().getList("");
        for (Map<String, Object> item : items) {
            String jsonString = objectMapper.writeValueAsString(item);
            JsonPath itemJsonPath = new JsonPath(jsonString);
            jsonPathListResp.add(itemJsonPath);
        }

        //Iterate each POST request data and verify it's available in GET response
        for (int i = 0; i < listOfJsonMapReq.size(); i++) {
            System.out.println("Processing from list of items #" + (i + 1));
            JsonPath jsonPathReq = new JsonPath(objectMapper.writeValueAsString(listOfJsonMapReq.get(i)));
            verifyGetObjectResponseBody(jsonPathReq, jsonPathListResp.get(i), objectIds.get(i));
        }
    }

    @When("^the request to remove the (?:item|id(?: \"([^\"]+)\")?) is made$")

    public void the_request_to_remove_the_item_is_made(String id) {

        response = RestAssured.given()
                .delete((baseUrl.concat("/").concat(id != null ? id : objectIds.getFirst())));

        System.out.println("Response is " + response.prettyPrint());
    }

    @Then("received object doesn't found")
    public void received_object_doesnt_found() {

        String expectedBody = "{\n" +
                "    \"error\": \"Object with id = " + objectIds.getFirst() + " doesn't exist.\"\n" +
                "}";
        assertEquals("Object doesn't found message not matching", expectedBody, response.getBody().asString());
    }

    @Then("received message as reserved id's cannot delete for id {string}")
    public void received_message_as_reserved_ids_cannot_delete(String id) {

        String expectedBody = "{\n" +
                "    \"error\": \"" + id + " is a reserved id and the data object of it cannot be deleted. You can create your own new object via POST request and try to send a DELETE request with new generated object id.\"\n" +
                "}";
        assertEquals("Message for reserve ids cannot delete , not matching", expectedBody, response.getBody().asString());
    }


    @Then("verify the delete response body")
    public void verify_the_delete_response_body() {

        JsonPath jsonPathRes = response.jsonPath();
        String expectedBody = "Object with id = " + objectIds.getFirst() + " has been deleted.";
        assertEquals("Delete Response body not matched", expectedBody, jsonPathRes.getString("message"));
        //Remove the createdItemId as this is already deleted and no required to use in @AfterAll
        Hooks.allObjectIds.remove(createdItemId);
    }

    @When("I hit API to list all objects")
    public void i_hit_api_to_list_all_objects() {
        response = RestAssured.given()
                .get(baseUrl);
        System.out.println("Response is " + response.prettyPrint());
    }

    @Then("verify all the default {int} objects retrieved as in {string}")
    public void verify_all_the_objects_retrieved(int defaultNumber, String responseJson) throws IOException {
        assertFalse(response.jsonPath().getList("$").isEmpty());
        assertEquals(defaultNumber, (response.jsonPath().getList("$").size()));
        String path = SOURCE_RESPONSE_PATH.concat(responseJson);
        String expectedResponse = new String(Files.readAllBytes(Paths.get(path)));
        assertEquals("Response not matching", objectMapper.readTree(expectedResponse), objectMapper.readTree(response.getBody().asString()));
    }


    /**
     * This function verify the POST call response body
     *
     * @param jsonPathReq is the request body mapped as JsonPath
     * @param jsonPathRes is the response body mapped as JsonPath
     */

    public void verifyPostObjectResponseBody(JsonPath jsonPathReq, JsonPath jsonPathRes) {
        assertTrue("ID should match regex ^[a-zA-Z0-9]{32}$", jsonPathRes.getString("id").matches("^[a-zA-Z0-9]{32}$"));
        assertTrue("Created at should match regex ^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\+00:00$", jsonPathRes.getString("createdAt").matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\+00:00$"));
        //name & data should verify only if that exists in request payload.
        if (jsonPathReq.getString("name") != null)
            assertEquals("Name should match", jsonPathReq.getString("name"), jsonPathRes.getString("name"));
        if (jsonPathReq.getString("data") != null)
            assertEquals("Data block should match", jsonPathReq.getString("data").toString(), jsonPathRes.getString("data"));
    }


    /**
     * This function verify the individual object data in the response body to GET call
     *
     * @param jsonPathReq is the request body mapped as JsonPath
     * @param jsonPathRes is the response body mapped as JsonPath
     * @param expectedId  is the objectID
     */

    public void verifyGetObjectResponseBody(JsonPath jsonPathReq, JsonPath jsonPathRes, String expectedId) {
        assertEquals("Id should match", expectedId, jsonPathRes.get("id"));
        //name & data should verify only if that exists in request payload.
        if (jsonPathReq.getString("name") != null)
            assertEquals("Name should match", jsonPathReq.getString("name"), jsonPathRes.get("name"));
        Map<String, Object> expectedData = jsonPathReq.getMap("data");
        Map<String, Object> actualData = jsonPathRes.get("data");
        if (jsonPathReq.getString("data") != null)
            assertEquals("Data block should match", expectedData, actualData);
    }
}