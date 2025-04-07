@GetObject @regression
Feature: This feature to cover the scenarios of GET call to get individual or multiple object details

  Scenario: Ability to return an item
    Given an object ,"iphone11.json" is created
    When the request to add the item is made
    Then a 200 response code is returned
    And object is added
    When I get the item by its Id
    Then the response matched with the id

  Scenario: Ability to return list of multiple items
    Given an object ,"iphone11.json" is created
    When the request to add the item is made
    Then a 200 response code is returned
    And object is added
    Given an object ,"iphone11.json" is created
    When the request to add the item is made
    Then a 200 response code is returned
    And object is added
    When I get the item by its id list
    Then the response returns a list of items

  Scenario: Object not found for incorrect Id
    When I get the item by its Id "111"
    Then a 404 response code is returned
    Then received object not found message for id "111"

