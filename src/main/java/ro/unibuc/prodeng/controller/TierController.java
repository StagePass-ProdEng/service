package ro.unibuc.prodeng.controller;
import ro.unibuc.prodeng.model.Tier;
import ro.unibuc.prodeng.request.CreateTierRequest;
import ro.unibuc.prodeng.response.TierResponse;
import ro.unibuc.prodeng.service.TierService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/tiers") 
public class TierController {

    private final TierService service;

    public TierController(TierService service) {
        this.service = service;
    }

    @PostMapping
    public TierResponse create(@Valid @RequestBody CreateTierRequest request) {
        Tier tier = new Tier();
        tier.setEventId(request.getEventId());
        tier.setTierName(request.getTierName());
        tier.setCapacity(request.getCapacity());
        tier.setPrice(request.getPrice());
        tier.setSold(0);
        Tier savedTier=service.createTier(tier);
        return new TierResponse(savedTier.getId(), savedTier.getEventId(), savedTier.getTierName(), savedTier.getCapacity(), savedTier.getSold(), savedTier.getPrice());
    }

    @GetMapping("/event/{eventId}") 
    public List<Tier> getByEvent(@PathVariable String eventId) {
        return service.getTiersByEvent(eventId);
    }

    @PatchMapping("/{id}/capacity")
    public Tier updateCapacity(@PathVariable String id, @RequestParam Integer newCapacity) {
        return service.updateTierCapacity(id, newCapacity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteTier(id);
    }
}
