package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.unibuc.prodeng.exception.TicketCheckInException;
import ro.unibuc.prodeng.model.Order;
import ro.unibuc.prodeng.model.Ticket;
import ro.unibuc.prodeng.repository.OrderRepository;
import ro.unibuc.prodeng.repository.TicketRepository;
import ro.unibuc.prodeng.request.OrderRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private TierService tierService;

    @InjectMocks private OrderService orderService;

    @Test
    void testPlaceOrder_validRequest_returnsCreatedOrder() {
        // Arrange
        OrderRequest request = new OrderRequest("event1", "tier1", "user1");
        Order savedOrder = new Order();
        savedOrder.setId("order123");
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> {
            Ticket t = i.getArgument(0);
            t.setId("ticket123");
            return t;
        });

        // Act
        Order result = orderService.placeOrder(request);

        // Assert
        assertNotNull(result);
        assertEquals("ticket123", result.getTicketId());
        verify(tierService, times(1)).consumeStock("tier1", 1);
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void testCheckInTicket_validTicket_updatesStatus() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId("t1");
        ticket.setCheckedIn(false);
        when(ticketRepository.findById("t1")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Ticket result = orderService.checkInTicket("t1");

        // Assert
        assertTrue(result.isCheckedIn());
        assertNotNull(result.getCheckInTime());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void testCheckInTicket_alreadyCheckedIn_throwsException() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setCheckedIn(true);
        ticket.setCheckInTime(LocalDateTime.now());
        when(ticketRepository.findById("t1")).thenReturn(Optional.of(ticket));

        // Act & Assert
        assertThrows(TicketCheckInException.class, () -> orderService.checkInTicket("t1"));
    }

    @Test
    void testGetOrdersByUser_returnsList() {
        // Arrange
        when(orderRepository.findByUserId("user1")).thenReturn(List.of(new Order()));
        
        // Act
        var result = orderService.getOrdersByUser("user1");
        
        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetCheckedInCountForEvent_returnsCorrectCount() {
        // Arrange
        Order o1 = new Order(); o1.setTicketId("t1");
        Ticket t1 = new Ticket(); t1.setCheckedIn(true);
        
        when(orderRepository.findByEventId("e1")).thenReturn(List.of(o1));
        when(ticketRepository.findById("t1")).thenReturn(Optional.of(t1));
        
        // Act
        long count = orderService.getCheckedInCountForEvent("e1");
        
        // Assert
        assertEquals(1, count);
    }

    @Test
    void testCheckInTicket_notFound_throwsException() {
        // Arrange
        when(ticketRepository.findById("t99")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(TicketCheckInException.class, () -> orderService.checkInTicket("t99"));
    }
    
}