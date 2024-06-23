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
                    "Request26", "Request27", "Request28", "Request29", "Request30",
                    "Request31", "Request32", "Request33", "Request34", "Request35",
                    "Request36", "Request37", "Request38", "Request39", "Request40",
                    "Request41", "Request42", "Request43", "Request44", "Request45",
                    "Request46", "Request47", "Request48", "Request49", "Request50",
                    "Request51", "Request52", "Request53", "Request54",
                    "Request55", "Request56", "Request57", "Request58", "Request59",
                    "Request60", "Request61", "Request62", "Request63", "Request64",
                    "Request65", "Request66", "Request67", "Request68", "Request69"
                    );

            System.gc();

            // Concurrent Load Balancer execution
            long timeConcurrent = executeLoadBalancer(new ConcurrentLoadBalancer(concurrentServerUrls), requests, "Concurrente");
            stopServers(concurrentServer1, concurrentServer2, concurrentServer3, concurrentServer4);

            System.gc();
            try {
                Thread.sleep(10000); // 10 segundos de pausa
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            long timeParallel = executeLoadBalancer(new ParallelLoadBalancer(parallelServerUrls), requests, "Paralelo");
            stopServers(parallelServer1, parallelServer2, parallelServer3, parallelServer4);


            double speedUp = (double) timeConcurrent / timeParallel;
            System.out.println("Speed-Up (Concurrente vs Paralelo): " + speedUp);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long executeLoadBalancer(LoadBalancer loadBalancer, List<String> requests, String type)
            throws InterruptedException, ExecutionException {
        System.out.println("Arranca ejecución del Load Balancer " + type);
        //loadBalancer.startMonitoring();
        long startTime = System.currentTimeMillis();
        loadBalancer.distributeRequests(requests);
        long endTime = System.currentTimeMillis();
        //loadBalancer.stopMonitoring();
        System.out.println(type + " tiempo de ejecución: " + (endTime - startTime) + " ms");
        return endTime - startTime;
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
