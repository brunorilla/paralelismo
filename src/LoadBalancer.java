import java.util.List;
import java.util.concurrent.ExecutionException;

public interface LoadBalancer {
    void distributeRequests(List<String> requests) throws InterruptedException, ExecutionException;
    void startMonitoring();
    void stopMonitoring();
}