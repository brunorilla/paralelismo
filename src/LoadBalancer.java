import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

public class LoadBalancer extends RecursiveTask<Void> {
    private final List<ServerSimulado> servers;
    private final List<String> requests;
    private static final int THRESHOLD = 10;

    public LoadBalancer(List<ServerSimulado> servers, List<String> requests) {
        this.servers = servers;
        this.requests = requests;
    }

    @Override
    protected Void compute() {
        if (requests.size() <= THRESHOLD) {
            processRequests(requests);
        } else {
            int mid = requests.size() / 2;
            LoadBalancer task1 = new LoadBalancer(servers, requests.subList(0, mid));
            LoadBalancer task2 = new LoadBalancer(servers, requests.subList(mid, requests.size()));
            invokeAll(task1, task2);
        }
        return null;
    }

    private void processRequests(List<String> requests) {
        for (String request : requests) {
            int serverIndex = ThreadLocalRandom.current().nextInt(servers.size());
            ServerSimulado server = servers.get(serverIndex);
            String response = server.handleRequest(request);
            System.out.println(response);
        }
    }
}
