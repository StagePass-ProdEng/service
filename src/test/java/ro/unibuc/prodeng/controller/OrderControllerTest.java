package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.prodeng.model.Order;
import ro.unibuc.prodeng.request.OrderRequest;
import ro.unibuc.prodeng.service.OrderService;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest("e1", "t1", "user1");
        Order order = new Order();
        order.setId("o1");
        order.setTicketId("t1");

        when(orderService.placeOrder(any(OrderRequest.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("o1"))
                .andExpect(jsonPath("$.ticketId").value("t1"));
    }

    @Test
    void testGetOrdersByUser() throws Exception {
        Order o1 = new Order();
        o1.setId("o1");
        when(orderService.getOrdersByUser("user1")).thenReturn(List.of(o1));

        mockMvc.perform(get("/api/orders/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("o1"));
    }

    @Test
    void testGetEventCheckInStatus() throws Exception {
        when(orderService.getCheckedInCountForEvent("e1")).thenReturn(5L);

        mockMvc.perform(get("/api/orders/event/e1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkedInCount").value(5));
    }
}