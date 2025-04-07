@RemoveObject @regression
Feature: This feature to cover the scenarios of DELETE call to remove the object data


  Scenario: Ability to remove the item from list and verify its removed
    Given an object ,"iphone11.json" is created
    When the request to add the item is made
    Then a 200 response code is returned
    And object is added
    When the request to remove the item is made
    Then verify the delete response body
    When I get the item by its Id
    Then received object not found message for id

  Scenario: Verify proper error received when when try to delete the object doesn't exist
    Given an object ,"iphone11.json" is created
    When the request to add the item is made
    Then a 200 response code is returned
    And object is added
    When the request to remove the item is made
    Then verify the delete response body
    When the request to remove the item is made
    Then a 404 response code is returned
    And received object doesn't found


  Scenario: Verify proper error received when try to delete reserved data

    When the request to remove the id "11" is made
    Then a 405 response code is returned
    And received message as reserved id's cannot delete for id "11"
