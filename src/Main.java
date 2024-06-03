import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<ServerSimulado> servers = List.of(
                new ServerSimulado(1),
                new ServerSimulado(2),
                new ServerSimulado(3),
                new ServerSimulado(4)
        );

        List<String> requests = List.of(
                "Request1", "Request2", "Request3", "Request4", "Request5",
                "Request6", "Request7", "Request8", "Request9", "Request10",
                "Request11", "Request12", "Request13", "Request14", "Request15"
        );

        // Ejecutar y medir el tiempo de la versión secuencial
        long startTimeSeq = System.currentTimeMillis();
        executeSequentially(servers, requests);
        long endTimeSeq = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución (secuencial): " + (endTimeSeq - startTimeSeq) + " ms");

        // Ejecutar y medir el tiempo de la versión concurrente
        long startTimeConc = System.currentTimeMillis();
        executeConcurrently(servers, requests);
        long endTimeConc = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución (concurrente): " + (endTimeConc - startTimeConc) + " ms");

        // Ejecutar y medir el tiempo de la versión paralela
        long startTimePar = System.currentTimeMillis();
        executeInParallel(servers, requests);
        long endTimePar = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución (paralelo): " + (endTimePar - startTimePar) + " ms");

        // Calcular y mostrar el Speed-Up
        double speedUpConc = (double) (endTimeSeq - startTimeSeq) / (endTimeConc - startTimeConc);
        double speedUpPar = (double) (endTimeSeq - startTimeSeq) / (endTimePar - startTimePar);
        System.out.println("Speed-Up (concurrente): " + speedUpConc);
        System.out.println("Speed-Up (paralelo): " + speedUpPar);
    }

    private static void executeSequentially(List<ServerSimulado> servers, List<String> requests) {
        for (String request : requests) {
            int serverIndex = ThreadLocalRandom.current().nextInt(servers.size());
            ServerSimulado server = servers.get(serverIndex);
            String response = server.handleRequest(request);
            System.out.println(response);
        }
    }

    private static void executeConcurrently(List<ServerSimulado> servers, List<String> requests) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (String request : requests) {
            int serverIndex = ThreadLocalRandom.current().nextInt(servers.size());
            ServerSimulado server = servers.get(serverIndex);
            Thread thread = new Thread(new RequestHandler(server, request));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static void executeInParallel(List<ServerSimulado> servers, List<String> requests) throws InterruptedException, ExecutionException {
        ForkJoinPool pool = new ForkJoinPool();
        LoadBalancer loadBalancer = new LoadBalancer(servers, requests);
        pool.submit(loadBalancer).get();
    }
}

class RequestHandler implements Runnable {
    private final ServerSimulado server;
    private final String request;

    public RequestHandler(ServerSimulado server, String request) {
        this.server = server;
        this.request = request;
    }

    @Override
    public void run() {
        String response = server.handleRequest(request);
        System.out.println(response);
    }
}
