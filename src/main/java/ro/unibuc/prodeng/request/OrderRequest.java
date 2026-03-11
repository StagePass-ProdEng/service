package ro.unibuc.prodeng.request;

public record OrderRequest(
    String eventId,
    String tierId,
    String userId
) {}