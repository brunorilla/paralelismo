import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Random;

public class HttpServerSimulado {
    private final int port;
    private HttpServer server;

    public HttpServerSimulado(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/handleRequest", new RequestHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private static class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = handleRequest();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String handleRequest() {
            // Realizar varias operaciones intensivas en CPU
            BigInteger factorialResult = factorial(10000);
            double matrixSumResult = sumLargeMatrix(1000);
            double primeSumResult = sumPrimes(10000);
            String processedString = processLargeString(10000);

            // Generar la respuesta HTML
            return "<html><body><h1>Response from server</h1>" +
                    "<p>Factorial calculated: " + factorialResult + "</p>" +
                    "<p>Matrix sum calculated: " + matrixSumResult + "</p>" +
                    "<p>Prime sum calculated: " + primeSumResult + "</p>" +
                    "<p>Processed string length: " + processedString.length() + "</p>" +
                    "</body></html>";
        }

        private BigInteger factorial(int n) {
            BigInteger result = BigInteger.ONE;
            for (int i = 1; i <= n; i++) {
                result = result.multiply(BigInteger.valueOf(i));
            }
            return result;
        }

        private double sumLargeMatrix(int size) {
            double[][] matrix = new double[size][size];
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = random.nextDouble();
                }
            }
            double sum = 0;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    sum += matrix[i][j];
                }
            }
            return sum;
        }

        private double sumPrimes(int n) {
            double sum = 0;
            for (int i = 2; i <= n; i++) {
                if (isPrime(i)) {
                    sum += i;
                }
            }
            return sum;
        }

        private boolean isPrime(int num) {
            if (num <= 1) return false;
            for (int i = 2; i <= Math.sqrt(num); i++) {
                if (num % i == 0) return false;
            }
            return true;
        }

        private String processLargeString(int size) {
            StringBuilder sb = new StringBuilder(size);
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                sb.append((char) ('A' + random.nextInt(26)));
            }
            return sb.toString().replaceAll("A", "Z");
        }
    }
}
