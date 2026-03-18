package ro.unibuc.prodeng.response;

import java.math.BigDecimal;

public record TierResponse(
    String id,
    String eventId,
    String tierName,
    Integer capacity,
    Integer sold,
    BigDecimal price
) {}