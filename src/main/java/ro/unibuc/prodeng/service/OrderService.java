package ro.unibuc.prodeng.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.unibuc.prodeng.exception.TicketCheckInException;
import ro.unibuc.prodeng.model.Order;
import ro.unibuc.prodeng.model.Ticket;
import ro.unibuc.prodeng.monitoring.MonitoringMetricsService;
import ro.unibuc.prodeng.repository.OrderRepository;
import ro.unibuc.prodeng.repository.TicketRepository;
import ro.unibuc.prodeng.request.OrderRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final TierService ticketTierService; 
    private final MonitoringMetricsService metrics;

    public OrderService(OrderRepository orderRepository, 
                        TicketRepository ticketRepository,
                        TierService ticketTierService,
                        MonitoringMetricsService metrics) {
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.ticketTierService = ticketTierService;
        this.metrics = metrics;
    }

    private <T> T withDbMetrics(Supplier<T> operation) {
        return metrics == null ? operation.get() : metrics.recordDbOperation(operation);
    }

    private void withDbMetrics(Runnable operation) {
        if (metrics == null) {
            operation.run();
            return;
        }
        metrics.recordDbOperation(operation);
    }

    @Transactional
    public Order placeOrder(OrderRequest request) {
        return withDbMetrics(() -> {
            ticketTierService.consumeStock(request.tierId(), 1);

            Order order = new Order();
            order.setEventId(request.eventId());
            order.setTierId(request.tierId());
            order.setUserId(request.userId());
            order.setOrderDate(LocalDateTime.now());
            order = orderRepository.save(order);

            Ticket ticket = new Ticket();
            ticket.setOrderId(order.getId());
            ticket.setEventId(request.eventId());
            ticket.setTierId(request.tierId());
            ticket.setCheckedIn(false);
            ticket = ticketRepository.save(ticket);

            order.setTicketId(ticket.getId());
            Order saved = orderRepository.save(order);
            if (metrics != null) {
                metrics.recordOrderPlaced();
            }
            return saved;
        });
    }

    public Ticket checkInTicket(String ticketId) {
        return withDbMetrics(() -> {
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
            Ticket checkedInTicket = ticketRepository.save(ticket);
            if (metrics != null) {
                metrics.recordCheckInCompleted();
            }
            return checkedInTicket;
        });
    }


    public List<Order> getOrdersByUser(String userId) {
        return withDbMetrics(() -> orderRepository.findByUserId(userId));
    }

    public long getCheckedInCountForEvent(String eventId) {
        return withDbMetrics(() -> {
            List<Order> orders = orderRepository.findByEventId(eventId);
            long count = 0;
            for (Order order : orders) {
                Ticket ticket = ticketRepository.findById(order.getTicketId()).orElse(null);
                if (ticket != null && ticket.isCheckedIn()) {
                    count++;
                }
            }
            return count;
        });
    }
}