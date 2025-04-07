**Java API Webservice Automation Pack**

This project is a REST API automation testing framework built using Java, Cucumber, and JUnit 5, managed via Maven. 
It is designed to validate and test RESTful services using BDD-style feature files.

Technologies/Libraries Used:
- Java 23
- Maven
- Cucumber (v7.14.0)
- JUnit 4 (4.13)
- JSON

How to Run the Tests: 
1. Clone the Repository
2. Build the project and download the depndencies
3. Run with Test - From commandline "mvn clean test" or run Junit Runner class "src/test/java/runner/TestRunner.java" directly 
4. After the test run, Cucumber Report can find under target/cucumber-report.html