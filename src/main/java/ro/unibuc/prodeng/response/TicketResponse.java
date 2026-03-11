package ro.unibuc.prodeng.response;

import java.time.LocalDateTime;

public record TicketResponse(
    String ticketId,
    String eventId,
    boolean checkedIn,
    LocalDateTime checkInTime,
    String message
) {}