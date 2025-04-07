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
2. Build the project and download the dependencies
3. Run the Test - From commandline "mvn clean test" or run Junit Runner class "src/test/java/runner/TestRunner.java" directly.<br> Note :  Use "@regression" tag to run all scenarios or use specific tags given in feature files for individual features <br>
4. After the test run, Cucumber Report can find under target/cucumber-report.html