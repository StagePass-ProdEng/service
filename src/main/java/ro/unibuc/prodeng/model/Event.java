package ro.unibuc.prodeng.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "events")
public class Event {
    
    @Id
    private String id;
    private String name;
    private Instant date;
    private String location;
    private String category;
    private boolean published;

    // Default constructor is needed for Spring/Jackson
    public Event() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Instant getDate() { return date; }
    public void setDate(Instant date) { this.date = date; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}