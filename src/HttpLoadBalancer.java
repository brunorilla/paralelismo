import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;

public class HttpLoadBalancer {
    private final List<String> serverUrls;
    private int currentServer = 0;

    public HttpLoadBalancer(List<String> serverUrls) {
        this.serverUrls = serverUrls;
    }

    public void distributeRequestsSequentially(List<String> requests) throws InterruptedException, ExecutionException, IOException {
        for (String request : requests) {
            System.out.println(sendRequest(getNextServerUrl(), request));
        }
    }

    public void distributeRequestsConcurrently(List<String> requests) throws InterruptedException, ExecutionException, IOException {
        ExecutorService executor = Executors.newFixedThreadPool(serverUrls.size());
        for (String request : requests) {
            Future<String> future = executor.submit(new RequestTask(getNextServerUrl(), request));
            System.out.println(future.get());
        }
        executor.shutdown();
    }

    public void distributeRequestsInParallel(List<String> requests) throws InterruptedException, ExecutionException, IOException {
        ForkJoinPool pool = new ForkJoinPool();
        List<Future<String>> futures = pool.invokeAll(requests.stream()
                .map(request -> (Callable<String>) () -> sendRequest(getNextServerUrl(), request))
                .toList());
        for (Future<String> future : futures) {
            System.out.println(future.get());
        }
    }

    private synchronized String getNextServerUrl() {
        String serverUrl = serverUrls.get(currentServer);
        currentServer = (currentServer + 1) % serverUrls.size();
        return serverUrl;
    }

    private static String sendRequest(String serverUrl, String request) throws IOException {
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

    private static class RequestTask implements Callable<String> {
        private final String serverUrl;
        private final String request;

        public RequestTask(String serverUrl, String request) {
            this.serverUrl = serverUrl;
            this.request = request;
        }

        @Override
        public String call() throws Exception {
            return sendRequest(serverUrl, request);
        }
    }
}