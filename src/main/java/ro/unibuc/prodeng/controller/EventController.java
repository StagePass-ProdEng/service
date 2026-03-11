package ro.unibuc.prodeng.controller;

import ro.unibuc.prodeng.service.EventService;
import ro.unibuc.prodeng.model.Event;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<Event> getEvents(@RequestParam(required = false) String category) {
        return service.getAllEvents(category);
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable String id) {
        return service.getEventById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Returns 201 Created instead of 200 OK
    public Event createEvent(@RequestBody Event event) {
        return service.createEvent(event);
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable String id, @RequestBody Event event) {
        return service.updateEvent(id, event);
    }

    @PatchMapping("/{id}/publish")
    public Event updatePublishStatus(@PathVariable String id, @RequestBody boolean published) {
        return service.updatePublishStatus(id, published);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204 No Content on success
    public void deleteEvent(@PathVariable String id) {
        service.deleteEvent(id);
    }
}
