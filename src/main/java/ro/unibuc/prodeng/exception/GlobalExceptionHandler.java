package ro.unibuc.prodeng.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import ro.unibuc.prodeng.monitoring.MonitoringMetricsService;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MonitoringMetricsService metrics;

    public GlobalExceptionHandler(MonitoringMetricsService metrics) {
        this.metrics = metrics;
    }

    // Check-in Exceptions
    @ExceptionHandler(TicketCheckInException.class)
    public ResponseEntity<Map<String, Object>> handleTicketCheckInException(TicketCheckInException ex) {
        if (metrics != null) {
            metrics.recordError("ticket_check_in_exception");
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // RuntimeExceptions (like "You already have a ticket for this tier")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        if (metrics != null) {
            metrics.recordError(ex.getClass().getSimpleName());
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Helper method to format the JSON 
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message); // <-- This forces your custom message to appear!

        return new ResponseEntity<>(body, status);
    }
}