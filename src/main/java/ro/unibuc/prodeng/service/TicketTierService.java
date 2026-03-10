package ro.unibuc.prodeng.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ro.unibuc.prodeng.model.TicketTier;
import ro.unibuc.prodeng.repository.TicketTierRepository;

@Service
public class TicketTierService {

    private final TicketTierRepository repository;

    public TicketTierService(TicketTierRepository repository) {
        this.repository = repository;
    }

    public TicketTier createTier(TicketTier tier) {
        return repository.save(tier);
    }

    public List<TicketTier> getTiersByEvent(String eventId) {
        return repository.findByEventId(eventId);
    }

    public TicketTier reserveTicket(String tierId, String userId) { 
    TicketTier tier = repository.findById(tierId)
            .orElseThrow(() -> new RuntimeException("Tier not found"));

    if (tier.getBuyerIds() != null && tier.getBuyerIds().contains(userId)) {
        throw new RuntimeException("You already have a ticket for this tier.");
    }

    if (!tier.hasAvailableTickets()) {
        throw new RuntimeException("SOLD OUT");
    }

    tier.getBuyerIds().add(userId);
    tier.setTicketsSold(tier.getTicketsSold() + 1);

    return repository.save(tier);
    }

    public void deleteTier(String id) {
    if (!repository.existsById(id)) {
        throw new RuntimeException("Tier not found, so it cannot be deleted.");
    }
    repository.deleteById(id);
    }
    
}
