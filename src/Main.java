import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        try {
            // Start servers for concurrent handling
            HttpServerConcurrente concurrentServer1 = new HttpServerConcurrente(8001);
            HttpServerConcurrente concurrentServer2 = new HttpServerConcurrente(8002);
            HttpServerConcurrente concurrentServer3 = new HttpServerConcurrente(8003);
            HttpServerConcurrente concurrentServer4 = new HttpServerConcurrente(8004);
            concurrentServer1.start();
            concurrentServer2.start();
            concurrentServer3.start();
            concurrentServer4.start();

            // Start servers for parallel handling
            HttpServerParalelo parallelServer1 = new HttpServerParalelo(8005);
            HttpServerParalelo parallelServer2 = new HttpServerParalelo(8006);
            HttpServerParalelo parallelServer3 = new HttpServerParalelo(8007);
            HttpServerParalelo parallelServer4 = new HttpServerParalelo(8008);
            parallelServer1.start();
            parallelServer2.start();
            parallelServer3.start();
            parallelServer4.start();

            // Server URLs setup
            List<String> concurrentServerUrls = List.of(
                    "http://localhost:8001",
                    "http://localhost:8002",
                    "http://localhost:8003",
                    "http://localhost:8004"
            );

            List<String> parallelServerUrls = List.of(
                    "http://localhost:8005",
                    "http://localhost:8006",
                    "http://localhost:8007",
                    "http://localhost:8008"
            );

            // Prepare requests
            List<String> requests = List.of(
                    "Request1", "Request2", "Request3", "Request4", "Request5",
                    "Request6", "Request7", "Request8", "Request9", "Request10",
                    "Request11", "Request12", "Request13", "Request14", "Request15",
                    "Request16", "Request17", "Request18", "Request19", "Request20",
                    "Request21", "Request22", "Request23", "Request24", "Request25",
                    "Request26", "Request27", "Request28", "Request29", "Request30"
            );

            // Concurrent Load Balancer execution
            executeLoadBalancer(new ConcurrentLoadBalancer(concurrentServerUrls), requests, "Concurrente");
            executeLoadBalancer(new ParallelLoadBalancer(parallelServerUrls), requests, "Paralelo");
            // Stop all servers
            stopServers(concurrentServer1, concurrentServer2, concurrentServer3, concurrentServer4);
            stopServers(parallelServer1, parallelServer2, parallelServer3, parallelServer4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeLoadBalancer(LoadBalancer loadBalancer, List<String> requests, String type)
            throws InterruptedException, ExecutionException {
        System.out.println("Arranca ejecución del Load balancer " + type);
        loadBalancer.startMonitoring();
        long startTime = System.currentTimeMillis();
        loadBalancer.distributeRequests(requests);
        long endTime = System.currentTimeMillis();
        loadBalancer.stopMonitoring();
        System.out.println(type + " tiempo de ejecución: " + (endTime - startTime) + " ms");
    }

    private static void stopServers(HttpServerConcurrente... servers) {
        for (HttpServerConcurrente server : servers) {
            server.stop();
        }
    }

    private static void stopServers(HttpServerParalelo... servers) {
        for (HttpServerParalelo server : servers) {
            server.stop();
        }
    }
}
