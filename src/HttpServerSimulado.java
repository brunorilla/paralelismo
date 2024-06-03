import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

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
            try {
                // Simular un tiempo de respuesta aleatorio
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));

                // Añadir una operación intensiva en CPU (calcular el factorial de un número grande)
                BigInteger result = factorial(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Response from server";
        }

        private BigInteger factorial(int n) {
            BigInteger result = BigInteger.ONE;
            for (int i = 1; i <= n; i++) {
                result = result.multiply(BigInteger.valueOf(i));
            }
            return result;
        }
    }
}
