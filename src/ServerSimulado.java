import java.util.concurrent.ThreadLocalRandom;
import java.math.BigInteger;

public class ServerSimulado {
    private final int id;

    public ServerSimulado(int id) {
        this.id = id;
    }

    public String handleRequest(String request) {
        try {
            // Simular un tiempo de respuesta aleatorio
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));

            // Añadir una operación intensiva en CPU (calcular el factorial de un número grande)
            BigInteger result = factorial(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Response from server " + id + " for request: " + request;
    }

    private BigInteger factorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 1; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
