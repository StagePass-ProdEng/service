package ro.unibuc.prodeng.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ticket-tiers")
public class TicketTier {
    @Id
    private String id;

    private String eventId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer capacity;
    private Integer ticketsSold=0;
    private String status="AVAILABLE";
    private List<String> buyerIds = new ArrayList<>();

    public boolean hasAvailableTickets(){
        return this.ticketsSold<this.capacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getTicketsSold() { return ticketsSold; }
    public void setTicketsSold(Integer ticketsSold) { this.ticketsSold = ticketsSold; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getBuyerIds() { return buyerIds; }
    public void setBuyerIds(List<String> buyerIds) { this.buyerIds = buyerIds; }
    
}
