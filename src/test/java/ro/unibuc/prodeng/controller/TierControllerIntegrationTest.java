package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.model.Tier;
import ro.unibuc.prodeng.repository.TierRepository;
import ro.unibuc.prodeng.request.CreateTierRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("TierController Integration Tests")
class TierControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TierRepository tierRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        tierRepository.deleteAll();
    }

    @Test
    void testCreateTier_validRequest_savesToRealDatabase() throws Exception {
        CreateTierRequest request = new CreateTierRequest();
        request.setEventId("event-100");
        request.setTierName("General Access");
        request.setCapacity(500);
        request.setPrice(new BigDecimal("150.00"));

        mockMvc.perform(post("/api/tiers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tierName").value("General Access"));

        List<Tier> savedTiers = tierRepository.findAll();
        assertEquals(1, savedTiers.size());
        assertEquals("event-100", savedTiers.get(0).getEventId());
        assertEquals(0, savedTiers.get(0).getSold());
    }

    @Test
    void testGetTiersByEvent_existingTiers_returnsFromDatabase() throws Exception {
        Tier tier = new Tier();
        tier.setEventId("event-200");
        tier.setTierName("VIP");
        tier.setCapacity(100);
        tier.setSold(0);
        tier.setPrice(new BigDecimal("300.00"));
        tierRepository.save(tier);

        mockMvc.perform(get("/api/tiers/event/event-200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tierName").value("VIP"))
                .andExpect(jsonPath("$[0].capacity").value(100));
    }
}