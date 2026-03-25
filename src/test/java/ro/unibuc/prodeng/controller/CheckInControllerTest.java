package ro.unibuc.prodeng.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.prodeng.model.Ticket;
import ro.unibuc.prodeng.service.OrderService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CheckInControllerTest {

    @Mock private OrderService orderService;
    @InjectMocks private CheckInController checkInController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(checkInController).build();
    }

    @Test
    void testScanTicket_validTicket_returnsOk() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setId("t1");
        ticket.setCheckedIn(true);
        
        when(orderService.checkInTicket("t1")).thenReturn(ticket);

        mockMvc.perform(post("/api/checkin/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value("t1"))
                .andExpect(jsonPath("$.message").value("Check-in successful! Allowed Entry."));
    }
}