package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.prodeng.model.Tier;
import ro.unibuc.prodeng.repository.TierRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TierServiceTest {

    @Mock
    private TierRepository tierRepository;

    @InjectMocks
    private TierService tierService;

    private Tier testTier;

    @BeforeEach
    void setUp() {
        //we create a tier for testing
        testTier = new Tier();
        testTier.setId("tier-1");
        testTier.setEventId("event-1");
        testTier.setTierName("VIP");
        testTier.setCapacity(100);
        testTier.setSold(10);
        testTier.setPrice(new BigDecimal("500.00"));
    }

    @Test
    void testCreateTier_savesAndReturnsTier() {
        when(tierRepository.save(any(Tier.class))).thenReturn(testTier);

        Tier result = tierService.createTier(testTier);

        assertNotNull(result);
        assertEquals("VIP", result.getTierName());
        verify(tierRepository, times(1)).save(testTier);
    }

    @Test
    void testGetTiersByEvent_returnsList() {
        when(tierRepository.findByEventId("event-1")).thenReturn(Arrays.asList(testTier));

        List<Tier> result = tierService.getTiersByEvent("event-1");

        assertEquals(1, result.size());
        assertEquals("tier-1", result.get(0).getId());
    }

    //tests for capacity update method

    @Test
    void testUpdateTierCapacity_validCapacity_updatesSuccessfully() {
        when(tierRepository.findById("tier-1")).thenReturn(Optional.of(testTier));
        when(tierRepository.save(any(Tier.class))).thenReturn(testTier);

        Tier result = tierService.updateTierCapacity("tier-1", 150);

        assertEquals(150, result.getCapacity());
        verify(tierRepository, times(1)).save(testTier);
    }

    @Test
    void testUpdateTierCapacity_capacityLessThanSold_throwsException() {
        when(tierRepository.findById("tier-1")).thenReturn(Optional.of(testTier));

        //we try to update the capacity to 5 after 10 tickets were sold
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> tierService.updateTierCapacity("tier-1", 5));
        
        assertTrue(exception.getMessage().contains("New capacity can't be smaller"));
        verify(tierRepository, never()).save(any(Tier.class));
    }

    //tests for stock consuming logic

    @Test
    void testConsumeStock_enoughTickets_updatesSold() {
        when(tierRepository.findById("tier-1")).thenReturn(Optional.of(testTier));
        
        tierService.consumeStock("tier-1", 5);

        assertEquals(15, testTier.getSold());//there were 10 tickets sold initially, and we sold another 5
        verify(tierRepository, times(1)).save(testTier);
    }

    @Test
    void testConsumeStock_notEnoughTickets_throwsException() {
        when(tierRepository.findById("tier-1")).thenReturn(Optional.of(testTier));
        
        //try to buy 100 tickets at an event with the capacity of 100 after we already bought 10
        assertThrows(RuntimeException.class, () -> tierService.consumeStock("tier-1", 100));
        
        verify(tierRepository, never()).save(any(Tier.class));
    }

    //tests for delete method

    @Test
    void testDeleteTier_noSoldTickets_deletesSuccessfully() {
        testTier.setSold(0); //set sold tickets to 0 so we can delete the tier
        when(tierRepository.findById("tier-1")).thenReturn(Optional.of(testTier));

        tierService.deleteTier("tier-1");

        verify(tierRepository, times(1)).deleteById("tier-1");
    }

    @Test
    void testDeleteTier_hasSoldTickets_throwsException() {
        when(tierRepository.findById("tier-1")).thenReturn(Optional.of(testTier));

        assertThrows(RuntimeException.class, () -> tierService.deleteTier("tier-1"));
        
        verify(tierRepository, never()).deleteById(anyString());
    }
}