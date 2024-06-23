import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentLoadBalancer implements LoadBalancer {
    private List<String> serverUrls;
    private int currentServerIndex = 0;
    private ExecutorService executor;
    private ScheduledExecutorService monitorExecutor;
    private EnhancedMonitoring monitoring;

    public ConcurrentLoadBalancer(List<String> serverUrls) {
        this.serverUrls = serverUrls;
        this.executor = Executors.newFixedThreadPool(serverUrls.size());
        this.monitorExecutor = Executors.newScheduledThreadPool(1);
        startMonitoring();
        this.monitoring = new EnhancedMonitoring();
    }

    public void distributeRequests(List<String> requests) throws InterruptedException, ExecutionException {
        //monitoring.startMonitoring();
        List<Future<String>> futures = new ArrayList<>();

        for (String request : requests) {
            String serverUrl = getNextServerUrl();
            futures.add(executor.submit(() -> sendRequest(serverUrl, request)));
        }

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }
        executor.shutdown();
        //monitorExecutor.shutdown();
        //monitoring.stopMonitoring();
    }

    private synchronized String getNextServerUrl() {
        String serverUrl = serverUrls.get(currentServerIndex);
        currentServerIndex = (currentServerIndex + 1) % serverUrls.size();
        return serverUrl;
    }

    private String sendRequest(String serverUrl, String request) throws IOException {
        URL url = new URL(serverUrl + "/handleRequest");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            return "Success: " + request;
        } else {
            return "Failed: " + request;
        }
    }

    public void startMonitoring() {
        monitorExecutor.scheduleAtFixedRate(() -> {
            int activeThreads = Thread.activeCount();
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            double systemLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
            System.out.println("Available processors (cores): " + availableProcessors);
            System.out.println("Active threads: " + activeThreads);
            System.out.println("System load average: " + systemLoad);
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopMonitoring() {
        monitorExecutor.shutdown();
    }
}
