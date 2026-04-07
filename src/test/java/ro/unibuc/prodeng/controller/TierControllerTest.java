package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.prodeng.model.Tier;
import ro.unibuc.prodeng.request.CreateTierRequest;
import ro.unibuc.prodeng.service.TierService;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
class TierControllerTest {

    //comment for jenkins test

    @Mock
    private TierService tierService;

    @InjectMocks
    private TierController tierController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private Tier testTier;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tierController).build();

        testTier = new Tier();
        testTier.setId("tier-1");
        testTier.setEventId("event-1");
        testTier.setTierName("VIP");
        testTier.setCapacity(100);
        testTier.setSold(0);
        testTier.setPrice(new BigDecimal("500.00"));
    }

    @Test
    void testCreateTier_returnsTierResponse() throws Exception {
        CreateTierRequest request = new CreateTierRequest();
        request.setEventId("event-1");
        request.setTierName("VIP");
        request.setCapacity(100);
        request.setPrice(new BigDecimal("500.00"));

        when(tierService.createTier(any(Tier.class))).thenReturn(testTier);

        mockMvc.perform(post("/api/tiers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("tier-1")))
                .andExpect(jsonPath("$.tierName", is("VIP")));
    }

    @Test
    void testGetTiersByEvent_returnsList() throws Exception {
        when(tierService.getTiersByEvent("event-1")).thenReturn(Arrays.asList(testTier));

        mockMvc.perform(get("/api/tiers/event/event-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("tier-1")));
    }

    @Test
    void testUpdateCapacity_returnsUpdatedTier() throws Exception {
        testTier.setCapacity(150);
        when(tierService.updateTierCapacity(eq("tier-1"), eq(150))).thenReturn(testTier);

        mockMvc.perform(patch("/api/tiers/tier-1/capacity")
                .param("newCapacity", "150")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity", is(150)));
    }

    @Test
    void testDeleteTier_returnsOk() throws Exception {
        doNothing().when(tierService).deleteTier("tier-1");

        mockMvc.perform(delete("/api/tiers/tier-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(tierService, times(1)).deleteTier("tier-1");
    }
}