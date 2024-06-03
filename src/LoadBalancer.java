import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LoadBalancer {
    private final List<String> serverUrls;
    private int currentServer = 0;
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

    public LoadBalancer(List<String> serverUrls) {
        this.serverUrls = serverUrls;
    }

    public void distributeRequestsSequentially(List<String> requests) throws IOException {
        for (String request : requests) {
            System.out.println(sendRequest(getNextServerUrl(), request));
        }
    }

    public void distributeRequestsConcurrently(List<String> requests) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(serverUrls.size());
        List<Future<String>> futures = new ArrayList<>();
        for (String request : requests) {
            Future<String> future = executor.submit(new RequestTask(getNextServerUrl(), request));
            futures.add(future);
        }

        monitorCpuUsage(executor);

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }
        executor.shutdown();
    }

    public void distributeRequestsInParallel(List<String> requests) throws InterruptedException, ExecutionException {
        ForkJoinPool pool = new ForkJoinPool();
        List<Future<String>> futures = pool.invokeAll(requests.stream()
                .map(request -> (Callable<String>) () -> sendRequest(getNextServerUrl(), request))
                .toList());

        monitorCpuUsage(pool);

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }
    }

    private synchronized String getNextServerUrl() {
        String serverUrl = serverUrls.get(currentServer);
        currentServer = (currentServer + 1) % serverUrls.size();
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

    private void monitorCpuUsage(ExecutorService executor) {
        ScheduledExecutorService monitorExecutor = Executors.newScheduledThreadPool(1);
        monitorExecutor.scheduleAtFixedRate(() -> {
            int activeThreads = Thread.activeCount();
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            double systemLoad = osBean.getSystemLoadAverage();
            System.out.println("Available processors (cores): " + availableProcessors);
            System.out.println("Active threads: " + activeThreads);
            System.out.println("System load average: " + systemLoad);
        }, 0, 1, TimeUnit.SECONDS);

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        monitorExecutor.shutdown();
    }

    private void monitorCpuUsage(ForkJoinPool pool) {
        ScheduledExecutorService monitorExecutor = Executors.newScheduledThreadPool(1);
        monitorExecutor.scheduleAtFixedRate(() -> {
            int activeThreads = Thread.activeCount();
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            double systemLoad = osBean.getSystemLoadAverage();
            System.out.println("Available processors (cores): " + availableProcessors);
            System.out.println("Active threads: " + activeThreads);
            System.out.println("System load average: " + systemLoad);
        }, 0, 1, TimeUnit.SECONDS);

        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        monitorExecutor.shutdown();
    }

    private class RequestTask implements Callable<String> {
        private final String serverUrl;
        private final String request;

        public RequestTask(String serverUrl, String request) {
            this.serverUrl = serverUrl;
            this.request = request;
        }

        @Override
        public String call() throws IOException {
            return sendRequest(serverUrl, request);
        }
    }
}