Feature: Event Management API

  Scenario: Successfully create and publish a new event
    Given the database is empty of events
    When I send a POST request to "/api/events" with the following data:
      | name         | location  | category  | published |
      | Java Meetup  | Bucharest | Tech      | true      |
    Then the API should return a 201 Created status
    And the database should have 1 event named "Java Meetup"