package ro.unibuc.prodeng.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class EventControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository; // Required for DB state verification

    @Test
    void shouldCreateEventAndVerifyDatabaseState() throws Exception {
        // 1. Arrange
        eventRepository.deleteAll(); // Clean state
        
        String eventPayload = """
            {
                "name": "Spring Boot Workshop",
                "date": "2026-05-10T10:00:00Z",
                "location": "Bucharest",
                "category": "Education",
                "published": true
            }
            """;

        // 2. Act 
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventPayload))
                .andExpect(status().isCreated());

        // 3. Assert (Verification of database state)
        assertThat(eventRepository.count()).isEqualTo(1);
        
        Event savedEvent = eventRepository.findAll().get(0);
        assertThat(savedEvent.getName()).isEqualTo("Spring Boot Workshop");
        assertThat(savedEvent.getLocation()).isEqualTo("Bucharest");
        assertThat(savedEvent.isPublished()).isTrue();
    }
}