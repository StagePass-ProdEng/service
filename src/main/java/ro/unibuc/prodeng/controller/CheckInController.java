package ro.unibuc.prodeng.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.prodeng.model.Ticket;
import ro.unibuc.prodeng.response.TicketResponse;
import ro.unibuc.prodeng.service.OrderService;

@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    private final OrderService orderService;

    public CheckInController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> scanTicket(@PathVariable String ticketId) {
        Ticket checkedInTicket = orderService.checkInTicket(ticketId);
        
        TicketResponse response = new TicketResponse(
                checkedInTicket.getId(),
                checkedInTicket.getEventId(),
                checkedInTicket.isCheckedIn(),
                checkedInTicket.getCheckInTime(),
                "Check-in successful! Allowed Entry."
        );
        
        return ResponseEntity.ok(response);
    }
}