import java.util.concurrent.ThreadLocalRandom;

public class ServerSimulado {
    private final int id;

    public ServerSimulado(int id) {
        this.id = id;
    }

    public String handleRequest(String request) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Response from server " + id + " for request: " + request;
    }
}
