package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ro.unibuc.prodeng.model.Event;
import ro.unibuc.prodeng.repository.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository repository;

    @InjectMocks
    private EventService service;

    @Test
    void getAllEvents_withCategory_shouldReturnFilteredList() {
        Event event = new Event();
        event.setCategory("Tech");
        when(repository.findByCategory("Tech")).thenReturn(List.of(event));

        List<Event> result = service.getAllEvents("Tech");

        assertEquals(1, result.size());
        assertEquals("Tech", result.get(0).getCategory());
        verify(repository).findByCategory("Tech");
    }

    @Test
    void getEventById_whenNotFound_shouldThrowException() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.getEventById("999"));
    }

    @Test
    void updateEvent_shouldUpdateAndSave() {
        // Arrange
        Event existingEvent = new Event();
        existingEvent.setId("1");
        existingEvent.setName("Old Name");

        Event updatedInfo = new Event();
        updatedInfo.setName("New Name");
        updatedInfo.setCategory("Tech");

        when(repository.findById("1")).thenReturn(Optional.of(existingEvent));
        when(repository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Event result = service.updateEvent("1", updatedInfo);

        // Assert
        assertEquals("New Name", result.getName());
        assertEquals("Tech", result.getCategory());
        verify(repository).save(existingEvent);
    }

    @Test
    void updatePublishStatus_shouldUpdateAndSave() {
        // Arrange
        Event existingEvent = new Event();
        existingEvent.setId("2");
        existingEvent.setPublished(false);

        when(repository.findById("2")).thenReturn(Optional.of(existingEvent));
        when(repository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Event result = service.updatePublishStatus("2", true);

        // Assert
        assertTrue(result.isPublished());
        verify(repository).save(existingEvent);
    }

    @Test
    void deleteEvent_whenExists_shouldDelete() {
        // Arrange
        when(repository.existsById("3")).thenReturn(true);

        // Act
        service.deleteEvent("3");

        // Assert
        verify(repository).deleteById("3");
    }

    @Test
    void deleteEvent_whenNotExists_shouldThrowException() {
        // Arrange
        when(repository.existsById("999")).thenReturn(false);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.deleteEvent("999"));
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    void getAllEvents_whenCategoryIsNull_shouldReturnAllEvents() {
        // Arrange
        Event event = new Event();
        when(repository.findAll()).thenReturn(List.of(event));

        // Act
        List<Event> result = service.getAllEvents(null);

        // Assert
        assertEquals(1, result.size());
        verify(repository).findAll(); // Verifies the branch that calls findAll()
    }

    @Test
    void getAllEvents_whenCategoryIsBlank_shouldReturnAllEvents() {
        // Arrange
        Event event = new Event();
        when(repository.findAll()).thenReturn(List.of(event));

        // Act
        List<Event> result = service.getAllEvents("   "); // Blank string

        // Assert
        assertEquals(1, result.size());
        verify(repository).findAll(); // Verifies the isBlank() check worked
    }

    @Test
    void createEvent_shouldSaveAndReturnEvent() {
        // Arrange
        Event newEvent = new Event();
        newEvent.setName("New Event");
        
        when(repository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Event result = service.createEvent(newEvent);

        // Assert
        assertEquals("New Event", result.getName());
        verify(repository).save(newEvent);
    }
}