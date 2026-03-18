package ro.unibuc.prodeng.model;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tiers")
public class Tier {
    @Id
    private String id;
    private String eventId;
    private String tierName;
    private Integer capacity;
    private Integer sold = 0;
    private BigDecimal price;

    public Tier() {}

    public boolean hasAvailableTickets(int quantity){
        return (this.sold + quantity) <= this.capacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTierName() { return tierName; }
    public void setTierName(String tierName) { this.tierName = tierName; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getSold() { return sold; }
    public void setSold(Integer sold) { this.sold = sold; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
}
