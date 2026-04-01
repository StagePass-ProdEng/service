package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.model.Order;
import ro.unibuc.prodeng.model.Ticket;
import ro.unibuc.prodeng.model.Tier;
import ro.unibuc.prodeng.repository.OrderRepository;
import ro.unibuc.prodeng.repository.TicketRepository;
import ro.unibuc.prodeng.repository.TierRepository;
import ro.unibuc.prodeng.request.OrderRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIntegrationTest extends IntegrationTestBase {

    @Autowired private MockMvc mockMvc;
    @Autowired private OrderRepository orderRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private TierRepository tierRepository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        // Ensure a clean database before and after each test
        orderRepository.deleteAll();
        ticketRepository.deleteAll();
        tierRepository.deleteAll();
    }

    @Test
    void testPlaceOrder_Integration_VerifiesDatabaseState() throws Exception {
        // Arrange
        Tier tier = new Tier();
        tier.setEventId("event-123");
        tier.setTierName("VIP");
        tier.setCapacity(50);
        tier.setSold(0);
        tier.setPrice(new BigDecimal("100.0"));
        tier = tierRepository.save(tier); // Saved to actual Mongo container!

        OrderRequest request = new OrderRequest("event-123", tier.getId(), "user-1");

        // Act
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size(), "Order should be saved in DB");
        
        List<Ticket> tickets = ticketRepository.findAll();
        assertEquals(1, tickets.size(), "Ticket should be generated in DB");
        
        Tier updatedTier = tierRepository.findById(tier.getId()).orElseThrow();
        assertEquals(1, updatedTier.getSold(), "Tier stock should be consumed in DB");
    }

    @Test
    void testCheckInTicket_Integration_VerifiesDatabaseState() throws Exception {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setEventId("event-123");
        ticket.setTierId("tier-123");
        ticket.setCheckedIn(false);
        ticket = ticketRepository.save(ticket);

        // Act
        mockMvc.perform(post("/api/checkin/" + ticket.getId()))
                .andExpect(status().isOk());

        // Assert
        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertTrue(updatedTicket.isCheckedIn(), "Ticket should be marked as checked-in");
        assertNotNull(updatedTicket.getCheckInTime(), "Check-in time should be recorded");
    }

    @Test
    void testPlaceOrder_Integration_OutOfStock_ReturnsBadRequest() throws Exception {
        // Arrange: Create a tier with 0 capacity
        Tier tier = new Tier();
        tier.setEventId("event-123");
        tier.setTierName("VIP");
        tier.setCapacity(0); // SOLD OUT
        tier.setSold(0);
        tier = tierRepository.save(tier);

        OrderRequest request = new OrderRequest("event-123", tier.getId(), "user-1");

        // Act & Assert: Verify it returns 400 when out of stock
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCheckInTicket_Integration_DuplicateCheckIn_ReturnsBadRequest() throws Exception {
        // Arrange: Create a ticket that is ALREADY checked in
        Ticket ticket = new Ticket();
        ticket.setEventId("event-123");
        ticket.setCheckedIn(true); // Already checked in
        ticket = ticketRepository.save(ticket);

        // Act & Assert: Verify it returns 400 (Duplicate)
        mockMvc.perform(post("/api/checkin/" + ticket.getId()))
                .andExpect(status().isBadRequest());
    }
}