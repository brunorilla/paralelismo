import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;

public class ParallelLoadBalancer implements LoadBalancer{
    private List<String> serverUrls;
    private int currentServerIndex = 0;
    private ForkJoinPool pool;
    private ScheduledExecutorService monitorExecutor;
    private EnhancedMonitoring monitoring;


    public ParallelLoadBalancer(List<String> serverUrls) {
        this.serverUrls = serverUrls;
        this.pool = new ForkJoinPool();
        this.monitorExecutor = Executors.newScheduledThreadPool(1);
        this.monitoring = new EnhancedMonitoring();
        startMonitoring();
    }

    public void distributeRequests(List<String> requests) throws InterruptedException, ExecutionException {
        monitoring.startMonitoring();
        pool.submit(() -> requests.parallelStream().forEach(request -> {
            try {
                System.out.println(sendRequest(getNextServerUrl(), request));
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).get();

        pool.shutdown();
        monitorExecutor.shutdown();
        monitoring.stopMonitoring();
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
