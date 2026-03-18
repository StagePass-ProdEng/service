package ro.unibuc.prodeng.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import ro.unibuc.prodeng.model.Tier;
import org.springframework.data.mongodb.repository.MongoRepository;


@Repository
public interface TierRepository extends MongoRepository<Tier,String>{

    List<Tier> findByEventId(String eventId);
}
