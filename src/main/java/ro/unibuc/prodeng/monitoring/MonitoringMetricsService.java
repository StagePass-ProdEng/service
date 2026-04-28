package ro.unibuc.prodeng.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import ro.unibuc.prodeng.repository.TodoRepository;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Service
public class MonitoringMetricsService {

    private final MeterRegistry registry;
    private final AtomicInteger activeDbOperations = new AtomicInteger();

    public MonitoringMetricsService(MeterRegistry registry, TodoRepository todoRepository) {
        this.registry = registry;

        Gauge.builder("app_active_db_operations", activeDbOperations, AtomicInteger::get)
                .description("Number of active database operations in progress")
                .register(registry);

        Gauge.builder("app_open_todos", todoRepository, TodoRepository::countByDoneFalse)
                .description("Number of todos that are not yet marked as done")
                .register(registry);
    }

    public void recordUserCreated() {
        registry.counter("app_users_created_total").increment();
    }

    public void recordOrderPlaced() {
        registry.counter("app_orders_placed_total").increment();
    }

    public void recordTodoCreated() {
        registry.counter("app_todos_created_total").increment();
    }

    public void recordCheckInCompleted() {
        registry.counter("app_checkins_completed_total").increment();
    }

    public void recordError(String type) {
        registry.counter("app_errors_total", "type", type).increment();
    }

    public Timer.Sample startRequestTimer() {
        return Timer.start(registry);
    }

    public void recordRequestDuration(Timer.Sample sample, String method, String status) {
        sample.stop(Timer.builder("app_request_duration_seconds")
                .description("Duration of HTTP requests handled by the service")
                .tag("method", method)
                .tag("status", status)
                .register(registry));
    }

    public <T> T recordDbOperation(Supplier<T> operation) {
        activeDbOperations.incrementAndGet();
        try {
            return operation.get();
        } finally {
            activeDbOperations.decrementAndGet();
        }
    }

    public void recordDbOperation(Runnable operation) {
        activeDbOperations.incrementAndGet();
        try {
            operation.run();
        } finally {
            activeDbOperations.decrementAndGet();
        }
    }
}