package ro.unibuc.prodeng.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ro.unibuc.prodeng.model.Ticket;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {
}