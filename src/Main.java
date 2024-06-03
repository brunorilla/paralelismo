import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // Iniciar servidores HTTP simulados
        HttpServerSimulado server1 = new HttpServerSimulado(8001);
        HttpServerSimulado server2 = new HttpServerSimulado(8002);
        HttpServerSimulado server3 = new HttpServerSimulado(8003);
        HttpServerSimulado server4 = new HttpServerSimulado(8004);
        HttpServerSimulado server5 = new HttpServerSimulado(8005);
        HttpServerSimulado server6 = new HttpServerSimulado(8006);
        HttpServerSimulado server7 = new HttpServerSimulado(8007);
        HttpServerSimulado server8 = new HttpServerSimulado(8008);

        server1.start();
        server2.start();
        server3.start();
        server4.start();
        server5.start();
        server6.start();
        server7.start();
        server8.start();

        List<String> serverUrls = List.of(
                "http://localhost:8001",
                "http://localhost:8002",
                "http://localhost:8003",
                "http://localhost:8004",
                "http://localhost:8005",
                "http://localhost:8006",
                "http://localhost:8007",
                "http://localhost:8008"
        );

        List<String> requests = List.of(
                "Request1", "Request2", "Request3", "Request4", "Request5",
                "Request6", "Request7", "Request8", "Request9", "Request10",
                "Request11", "Request12", "Request13", "Request14", "Request15",
                "Request16", "Request17", "Request18", "Request19", "Request20",
                "Request21", "Request22", "Request23", "Request24", "Request25",
                "Request26", "Request27", "Request28", "Request29", "Request30"
        );

        LoadBalancer loadBalancer = new LoadBalancer(serverUrls);

        // Medir tiempo de ejecución secuencial
        long startTimeSeq = System.currentTimeMillis();
        loadBalancer.distributeRequestsSequentially(requests);
        long endTimeSeq = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución (secuencial): " + (endTimeSeq - startTimeSeq) + " ms");

        // Medir tiempo de ejecución concurrente
        long startTimeConc = System.currentTimeMillis();
        loadBalancer.distributeRequestsConcurrently(requests);
        long endTimeConc = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución (concurrente): " + (endTimeConc - startTimeConc) + " ms");

        // Medir tiempo de ejecución paralela
        long startTimePar = System.currentTimeMillis();
        loadBalancer.distributeRequestsInParallel(requests);
        long endTimePar = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución (paralelo): " + (endTimePar - startTimePar) + " ms");

        // Calcular y mostrar el Speed-Up
        double speedUpConc = (double) (endTimeSeq - startTimeSeq) / (endTimeConc - startTimeConc);
        double speedUpPar = (double) (endTimeSeq - startTimeSeq) / (endTimePar - startTimePar);
        System.out.println("Speed-Up (concurrente): " + speedUpConc);
        System.out.println("Speed-Up (paralelo): " + speedUpPar);

        // Detener los servidores
        server1.stop();
        server2.stop();
        server3.stop();
        server4.stop();
        server5.stop();
        server6.stop();
        server7.stop();
        server8.stop();
    }
}