package ro.unibuc.prodeng.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import ro.unibuc.prodeng.model.TicketTier;
import org.springframework.data.mongodb.repository.MongoRepository;


@Repository
public interface TicketTierRepository extends MongoRepository<TicketTier,String>{

    List<TicketTier> findByEventId(String eventId);
}
