package ro.unibuc.prodeng.e2e.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import ro.unibuc.prodeng.model.Event;
import ro.unibuc.prodeng.repository.EventRepository;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EventSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EventRepository eventRepository;

    private ResponseEntity<Event> response;

    @Given("the database is empty of events")
    public void theDatabaseIsEmptyOfEvents() {
        eventRepository.deleteAll();
    }

    @When("I send a POST request to {string} with the following data:")
    public void iSendAPOSTRequestToWithTheFollowingData(String endpoint, DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        
        Event newEvent = new Event();
        newEvent.setName(row.get("name"));
        newEvent.setLocation(row.get("location"));
        newEvent.setCategory(row.get("category"));
        newEvent.setPublished(Boolean.parseBoolean(row.get("published")));
        newEvent.setDate(Instant.now().plusSeconds(86400)); // Sets date to tomorrow
        
        response = restTemplate.postForEntity(endpoint, newEvent, Event.class);
    }

    @Then("the API should return a {int} Created status")
    public void theAPIShouldReturnACreatedStatus(int expectedStatus) {
        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
    }

    @And("the database should have {int} event named {string}")
    public void theDatabaseShouldHaveEventNamed(int expectedCount, String expectedName) {
        assertThat(eventRepository.findAll()).hasSize(expectedCount);
        assertThat(eventRepository.findAll().get(0).getName()).isEqualTo(expectedName);
    }
}