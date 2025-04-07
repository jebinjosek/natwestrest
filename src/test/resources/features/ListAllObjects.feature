@RemoveObject @regression
Feature: This feature is to verify service is able to list all objects

  Scenario: Verify ability to list all items
    When I hit API to list all objects
    Then a 200 response code is returned
    And verify all the default 13 objects retrieved as in "allObjectsResponse.json"