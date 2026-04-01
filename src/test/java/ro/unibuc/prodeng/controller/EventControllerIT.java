package ro.unibuc.prodeng.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.model.Event;
import ro.unibuc.prodeng.repository.EventRepository;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class EventControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        // Ensures a clean database state before every test
        eventRepository.deleteAll(); 
    }

    @Test
    void shouldCreateEventAndVerifyDatabaseState() throws Exception {
        // Arrange
        String eventPayload = """
            {
                "name": "Spring Boot Workshop",
                "date": "2026-05-10T10:00:00Z",
                "location": "Bucharest",
                "category": "Education",
                "published": true
            }
            """;

        // Act
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventPayload))
                .andExpect(status().isCreated());

        // Assert
        assertThat(eventRepository.count()).isEqualTo(1);
        Event savedEvent = eventRepository.findAll().get(0);
        assertThat(savedEvent.getName()).isEqualTo("Spring Boot Workshop");
        assertThat(savedEvent.getLocation()).isEqualTo("Bucharest");
        assertThat(savedEvent.isPublished()).isTrue();
    }

    @Test
    void shouldGetAllEvents() throws Exception {
        // Arrange
        Event event1 = new Event();
        event1.setName("Event 1");
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setName("Event 2");
        eventRepository.save(event2);

        // Act & Assert
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Event 1"))
                .andExpect(jsonPath("$[1].name").value("Event 2"));
    }

    @Test
    void shouldGetEventsByCategory() throws Exception {
       
        Event event1 = new Event();
        event1.setName("Tech Meetup");
        event1.setCategory("Tech");
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setName("Art Show");
        event2.setCategory("Art");
        eventRepository.save(event2);

        // Act & Assert
        mockMvc.perform(get("/api/events?category=Tech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Tech Meetup"));
    }

    @Test
    void shouldGetEventById() throws Exception {
        // Arrange
        Event event = new Event();
        event.setName("Target Event");
        Event savedEvent = eventRepository.save(event);

        // Act & Assert
        mockMvc.perform(get("/api/events/{id}", savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Target Event"));
    }

    @Test
    void shouldUpdateEventAndVerifyDatabaseState() throws Exception {
        // Arrange
        Event event = new Event();
        event.setName("Old Name");
        Event savedEvent = eventRepository.save(event);

        String updatePayload = """
            {
                "name": "Updated Name",
                "date": "2026-06-10T10:00:00Z",
                "location": "Cluj",
                "category": "Tech",
                "published": false
            }
            """;

        // Act
        mockMvc.perform(put("/api/events/{id}", savedEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
                .andExpect(status().isOk());

        // Assert
        Event updatedEvent = eventRepository.findById(savedEvent.getId()).orElseThrow();
        assertThat(updatedEvent.getName()).isEqualTo("Updated Name");
        assertThat(updatedEvent.getLocation()).isEqualTo("Cluj");
    }

    @Test
    void shouldUpdatePublishStatusAndVerifyDatabaseState() throws Exception {
        // Arrange
        Event event = new Event();
        event.setName("Draft Event");
        event.setPublished(false);
        Event savedEvent = eventRepository.save(event);

        // Act
        mockMvc.perform(patch("/api/events/{id}/publish", savedEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("true"))
                .andExpect(status().isOk());

        // Assert
        Event updatedEvent = eventRepository.findById(savedEvent.getId()).orElseThrow();
        assertThat(updatedEvent.isPublished()).isTrue();
    }

    @Test
    void shouldDeleteEventAndVerifyDatabaseState() throws Exception {
        // Arrange
        Event event = new Event();
        event.setName("To Be Deleted");
        Event savedEvent = eventRepository.save(event);

        // Act
        mockMvc.perform(delete("/api/events/{id}", savedEvent.getId()))
                .andExpect(status().isNoContent()); // Because of @ResponseStatus(HttpStatus.NO_CONTENT)

        // Assert
        assertThat(eventRepository.existsById(savedEvent.getId())).isFalse();
    }
}