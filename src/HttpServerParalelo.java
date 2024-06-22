import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class HttpServerParalelo {
    private final int port;
    private HttpServer server;

    public HttpServerParalelo(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/handleRequest", new RequestHandler());
        server.setExecutor(null); // Utiliza un executor predeterminado
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private static class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String response = handleRequest();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

         public static boolean isPrime(int num) {
            if (num <= 1) return false;
            for (int i = 2; i <= Math.sqrt(num); i++) {
                if (num % i == 0) return false;
            }
            return true;
        }



        private String handleRequest() throws InterruptedException, ExecutionException {
            ForkJoinPool pool = new ForkJoinPool();

            Future<BigInteger> factorialFuture = pool.submit(() -> factorial(10000));
            Future<Double> matrixSumFuture = pool.submit(() -> sumLargeMatrix(1000));
            Future<Double> primeSumFuture = pool.submit(() -> sumPrimes(10000));
            Future<String> processedStringFuture = pool.submit(() -> processLargeString(10000));

            BigInteger factorialResult = factorialFuture.get();
            double matrixSumResult = matrixSumFuture.get();
            double primeSumResult = primeSumFuture.get();
            String processedString = processedStringFuture.get();

            pool.shutdown();

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
            ForkJoinTask<Double> task = new MatrixSumTask(matrix, 0, matrix.length);
            return ForkJoinPool.commonPool().invoke(task);
        }


        private double sumPrimes(int n) {
            return IntStream.rangeClosed(2, n)
                    .parallel()
                    .filter(RequestHandler::isPrime)
                    .asDoubleStream()
                    .sum();
        }



        private String processLargeString(int size) {
            StringBuilder sb = new StringBuilder(size);
            Random random = new Random();
            IntStream.range(0, size).parallel().forEach(i -> sb.append((char) ('A' + random.nextInt(26))));
            return sb.toString().replaceAll("A", "Z");
        }

        private static class MatrixSumTask extends RecursiveTask<Double> {
            private final double[][] matrix;
            private final int startRow, endRow;

            public MatrixSumTask(double[][] matrix, int startRow, int endRow) {
                this.matrix = matrix;
                this.startRow = startRow;
                this.endRow = endRow;
            }

            @Override
            protected Double compute() {
                if (endRow - startRow <= 10) {
                    double sum = 0;
                    for (int i = startRow; i < endRow; i++) {
                        for (double v : matrix[i]) {
                            sum += v;
                        }
                    }
                    return sum;
                } else {
                    int mid = startRow + (endRow - startRow) / 2;
                    MatrixSumTask leftTask = new MatrixSumTask(matrix, startRow, mid);
                    MatrixSumTask rightTask = new MatrixSumTask(matrix, mid, endRow);
                    leftTask.fork();
                    double rightResult = rightTask.compute();
                    double leftResult = leftTask.join();
                    return leftResult + rightResult;
                }
            }
        }
    }
}
