package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.prodeng.model.Event;
import ro.unibuc.prodeng.service.EventService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    void getEvents_shouldReturnOk() throws Exception {
        // Arrange
        Event event = new Event();
        event.setId("1");
        event.setName("Spring Workshop");
        
        when(eventService.getAllEvents(null)).thenReturn(List.of(event));

        // Act & Assert
        mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Spring Workshop")));

        verify(eventService, times(1)).getAllEvents(null);
    }

    @Test
    void createEvent_shouldReturnCreated() throws Exception {
        // Arrange
        Event newEvent = new Event();
        newEvent.setName("New Event");

        Event savedEvent = new Event();
        savedEvent.setId("123");
        savedEvent.setName("New Event");

        when(eventService.createEvent(any(Event.class))).thenReturn(savedEvent);

        // Act & Assert
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("123")))
                .andExpect(jsonPath("$.name", is("New Event")));
    }

    @Test
    void updateEvent_shouldReturnUpdatedEvent() throws Exception {
        // Arrange
        Event updatedEvent = new Event();
        updatedEvent.setName("Updated Workshop");

        when(eventService.updateEvent(eq("1"), any(Event.class))).thenReturn(updatedEvent);

        // Act & Assert
        mockMvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Workshop")));
    }

    @Test
    void deleteEvent_shouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/events/1"))
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteEvent("1");
    }

    @Test
    void getEvents_withCategoryParam_shouldReturnFiltered() throws Exception {
        // Arrange
        Event event = new Event();
        event.setName("Tech Meetup");
        when(eventService.getAllEvents("Tech")).thenReturn(List.of(event));

        // Act & Assert
        mockMvc.perform(get("/api/events")
                        .param("category", "Tech") // Simulates /api/events?category=Tech
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Tech Meetup")));

        verify(eventService, times(1)).getAllEvents("Tech");
    }

    @Test
    void getEvent_shouldReturnOk() throws Exception {
        // Arrange
        Event event = new Event();
        event.setId("1");
        event.setName("Spring Workshop");
        
        when(eventService.getEventById("1")).thenReturn(event);

        // Act & Assert
        mockMvc.perform(get("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Spring Workshop")));
                
        verify(eventService, times(1)).getEventById("1");
    }
}