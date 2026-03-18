package ro.unibuc.prodeng.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.prodeng.model.Order;
import ro.unibuc.prodeng.request.OrderRequest;
import ro.unibuc.prodeng.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        Order newOrder = orderService.placeOrder(request);
        return ResponseEntity.ok(newOrder);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/event/{eventId}/status")
    public ResponseEntity<Map<String, Long>> getEventCheckInStatus(@PathVariable String eventId) {
        long checkedIn = orderService.getCheckedInCountForEvent(eventId);
        Map<String, Long> response = new java.util.HashMap<>();
        response.put("checkedInCount", checkedIn);
        return ResponseEntity.ok(response);
    }
}