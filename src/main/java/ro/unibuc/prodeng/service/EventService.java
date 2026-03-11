package ro.unibuc.prodeng.service;

import ro.unibuc.prodeng.repository.EventRepository;
import ro.unibuc.prodeng.model.Event;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public List<Event> getAllEvents(String category) {
        if (category != null && !category.isBlank()) {
            return repository.findByCategory(category);
        }
        return repository.findAll();
    }

    public Event getEventById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with ID: " + id));
    }

    public Event createEvent(Event event) {
        // You could add business logic here, e.g., checking if the date is in the future
        return repository.save(event);
    }

    public Event updateEvent(String id, Event updatedEvent) {
        Event existing = getEventById(id);
        existing.setName(updatedEvent.getName());
        existing.setDate(updatedEvent.getDate());
        existing.setLocation(updatedEvent.getLocation());
        existing.setCategory(updatedEvent.getCategory());
        existing.setPublished(updatedEvent.isPublished());
        return repository.save(existing);
    }

    public Event updatePublishStatus(String id, boolean published) {
        Event existing = getEventById(id);
        existing.setPublished(published);
        return repository.save(existing);
    }

    public void deleteEvent(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        repository.deleteById(id);
    }
}
