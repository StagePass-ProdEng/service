package ro.unibuc.prodeng.repository;

import ro.unibuc.prodeng.model.Event;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    // Custom query method: Spring writes the query based on the method name!
    List<Event> findByCategory(String category);
}