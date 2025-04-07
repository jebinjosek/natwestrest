@AddObject @regression
Feature: This feature to cover the scenarios of POST call to add an object to the list

  Scenario Outline: Verify an object, <data> can be added
    Given an object ,"<data>" is created
    When the request to add the item is made
    Then a 200 response code is returned
    And object is added
    Examples:
      | data          |
      | iphone11.json |
      | iphone12.json |
      | iphone13.json |
      | empty.json    |

  Scenario: Verify an invalid object data cannot add -posting without body
    When the request to add the item is made
    Then a 400 response code is returned
    And object is not added