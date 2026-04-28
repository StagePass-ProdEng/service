package ro.unibuc.prodeng.service;

import ro.unibuc.prodeng.repository.EventRepository;
import ro.unibuc.prodeng.model.Event;
import ro.unibuc.prodeng.monitoring.MonitoringMetricsService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.function.Supplier;

@Service
public class EventService {

    private final EventRepository repository;
    private final MonitoringMetricsService metrics;

    public EventService(EventRepository repository, MonitoringMetricsService metrics) {
        this.repository = repository;
        this.metrics = metrics;
    }

    private <T> T withDbMetrics(Supplier<T> operation) {
        return metrics == null ? operation.get() : metrics.recordDbOperation(operation);
    }

    private void withDbMetrics(Runnable operation) {
        if (metrics == null) {
            operation.run();
            return;
        }
        metrics.recordDbOperation(operation);
    }

    public List<Event> getAllEvents(String category) {
        return withDbMetrics(() -> {
            if (category != null && !category.isBlank()) {
                return repository.findByCategory(category);
            }
            return repository.findAll();
        });
    }

    public Event getEventById(String id) {
        return withDbMetrics(() -> repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with ID: " + id)));
    }

    public Event createEvent(Event event) {
        // You could add business logic here, e.g., checking if the date is in the future
        return withDbMetrics(() -> repository.save(event));
    }

    public Event updateEvent(String id, Event updatedEvent) {
        return withDbMetrics(() -> {
            Event existing = getEventById(id);
            existing.setName(updatedEvent.getName());
            existing.setDate(updatedEvent.getDate());
            existing.setLocation(updatedEvent.getLocation());
            existing.setCategory(updatedEvent.getCategory());
            existing.setPublished(updatedEvent.isPublished());
            return repository.save(existing);
        });
    }

    public Event updatePublishStatus(String id, boolean published) {
        return withDbMetrics(() -> {
            Event existing = getEventById(id);
            existing.setPublished(published);
            return repository.save(existing);
        });
    }

    public void deleteEvent(String id) {
        withDbMetrics(() -> {
            if (!repository.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
            }
            repository.deleteById(id);
        });
    }
}
