package ro.unibuc.prodeng.controller;
import ro.unibuc.prodeng.model.TicketTier;
import ro.unibuc.prodeng.request.CreateTierRequest;
import ro.unibuc.prodeng.service.TicketTierService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-tiers") 
public class TicketTierController {

    private final TicketTierService service;

    public TicketTierController(TicketTierService service) {
        this.service = service;
    }

    @PostMapping
    public TicketTier create(@Valid @RequestBody CreateTierRequest request) {
        TicketTier tier = new TicketTier();
        tier.setEventId(request.getEventId());
        tier.setName(request.getName());
        tier.setPrice(request.getPrice());
        tier.setCapacity(request.getCapacity());
        tier.setTicketsSold(0);
        tier.setStatus("AVAILABLE");
        return service.createTier(tier);
    }

    @GetMapping("/event/{eventId}") 
    public List<TicketTier> getByEvent(@PathVariable String eventId) {
        return service.getTiersByEvent(eventId);
    }

    @PatchMapping("/{id}/reserve")
    public TicketTier reserve(@PathVariable String id, @RequestParam String userId) {
        return service.reserveTicket(id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteTier(id);
    }
}
