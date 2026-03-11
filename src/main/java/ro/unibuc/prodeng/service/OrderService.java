package ro.unibuc.prodeng.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.unibuc.prodeng.exception.TicketCheckInException;
import ro.unibuc.prodeng.model.Order;
import ro.unibuc.prodeng.model.Ticket;
import ro.unibuc.prodeng.repository.OrderRepository;
import ro.unibuc.prodeng.repository.TicketRepository;
import ro.unibuc.prodeng.request.OrderRequest;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final TicketTierService ticketTierService; 

    public OrderService(OrderRepository orderRepository, 
                        TicketRepository ticketRepository,
                        TicketTierService ticketTierService) {
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.ticketTierService = ticketTierService;
    }

    @Transactional
    public Order placeOrder(OrderRequest request) {
 
        ticketTierService.reserveTicket(request.tierId(), request.userId());

        // Create the Order
        Order order = new Order();
        order.setEventId(request.eventId());
        order.setTierId(request.tierId());
        order.setUserId(request.userId());
        order.setOrderDate(LocalDateTime.now());
        order = orderRepository.save(order);

        // Generate the Ticket for check-in
        Ticket ticket = new Ticket();
        ticket.setOrderId(order.getId());
        ticket.setEventId(request.eventId());
        ticket.setTierId(request.tierId());
        ticket.setCheckedIn(false);
        ticket = ticketRepository.save(ticket);

        order.setTicketId(ticket.getId()); 
        return orderRepository.save(order);
    }

    public Ticket checkInTicket(String ticketId) {

        if (ticketId == null || ticketId.isBlank()) {
            throw new TicketCheckInException("Ticket ID cannot be null or empty");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketCheckInException("Ticket not found with ID: " + ticketId));

        if (ticket.isCheckedIn()) {
            throw new TicketCheckInException("Duplicate check-in! Ticket was already used at " + ticket.getCheckInTime());
        }

        ticket.setCheckedIn(true);
        ticket.setCheckInTime(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
}