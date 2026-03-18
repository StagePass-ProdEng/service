package ro.unibuc.prodeng.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ro.unibuc.prodeng.model.Tier;
import ro.unibuc.prodeng.repository.TierRepository;

@Service
public class TierService {

    private final TierRepository repository;

    public TierService(TierRepository repository) {
        this.repository = repository;
    }

    public Tier createTier(Tier tier) {
        return repository.save(tier);
    }

    public List<Tier> getTiersByEvent(String eventId) {
        return repository.findByEventId(eventId);
    }

    public Tier updateTierCapacity(String id, Integer newCapacity) {
        Tier tier = repository.findById(id).orElseThrow(() -> new RuntimeException("Tier not found"));
        
        if (newCapacity < tier.getSold()) {
            throw new RuntimeException("New capacity can't be smaller than currently sold: " + tier.getSold());
        }
        
        tier.setCapacity(newCapacity);
        return repository.save(tier);
    }

    public void consumeStock(String tierId, int quantityToBuy) {
        Tier tier = repository.findById(tierId)
                .orElseThrow(() -> new RuntimeException("Tier not found with ID: " + tierId));

        if (!tier.hasAvailableTickets(quantityToBuy)) {
            throw new RuntimeException("Tier " + tier.getTierName() + " is sold out!");
        }

        tier.setSold(tier.getSold() + quantityToBuy);
        repository.save(tier);
    }

    public void deleteTier(String id) {
        Tier tier = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tier not found"));

        if (tier.getSold() > 0) {
            throw new RuntimeException("You can't delete the tier because there are sold tickets already!");
        }

        repository.deleteById(id);
    }
    
}
