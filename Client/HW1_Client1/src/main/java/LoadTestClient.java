import java.net.URI;
import java.net.http.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTestClient {

    private static final int INIT_PHASE_THREADS = 10;
    private static final int INIT_PHASE_CALLS = 100;
    public static final int MAX_RETRIES = 5;

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("Usage: LoadTestClient <threadGroupSize> <numThreadGroups> <delay> <baseURL>");
            return;
        }

        int threadGroupSize = Integer.parseInt(args[0]);
        int numThreadGroups = Integer.parseInt(args[1]);
        int delay = Integer.parseInt(args[2]);
        String baseURL = args[3];

        initializeTest(baseURL);
        System.out.println("Bootstrapping complete!");

        long startTime = System.currentTimeMillis();
        executeStressTest(baseURL, threadGroupSize, numThreadGroups, delay);
        long endTime = System.currentTimeMillis();

        double elapsedTime = (endTime - startTime) / 1000.0;
        System.out.println("Total Time: " + elapsedTime + " seconds");
        System.out.println("Throughput: " + RequestCounter.getTotalRequests() / elapsedTime + " requests/sec");
    }

    private static void initializeTest(String baseURL) {
        ExecutorService initService = Executors.newFixedThreadPool(INIT_PHASE_THREADS);
        for (int i = 0; i < INIT_PHASE_THREADS; i++) {
            initService.submit(new RequestTask(baseURL, INIT_PHASE_CALLS));
        }
        shutDownExecutor(initService);
    }

    private static void executeStressTest(String baseURL, int threadGroupSize, int numThreadGroups, int delay) throws InterruptedException {
        ExecutorService testService = Executors.newFixedThreadPool(threadGroupSize * numThreadGroups);
        for (int i = 0; i < numThreadGroups; i++) {
            for (int j = 0; j < threadGroupSize; j++) {
                testService.submit(new RequestTask(baseURL, 1000));
            }
            if (i < numThreadGroups - 1) {
                Thread.sleep(delay * 1000L);
            }
        }
        shutDownExecutor(testService);
    }

    private static void shutDownExecutor(ExecutorService service) {
        service.shutdown();
        try {
            if (!service.awaitTermination(1, TimeUnit.HOURS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

class RequestTask implements Runnable {
    private final String baseURL;
    private final int numRequests;
    private final WebAPI api;

    public RequestTask(String baseURL, int numRequests) {
        this.baseURL = baseURL;
        this.numRequests = numRequests;
        this.api = new WebAPI(baseURL);
    }

    @Override
    public void run() {
        for (int i = 0; i < numRequests; i++) {
            api.send("POST");
            api.send("GET");
            RequestCounter.incrementTotal();
        }
    }
}

class WebAPI {
    private final String baseURL;
    private final HttpClient client;

    public WebAPI(String baseURL) {
        this.baseURL = baseURL;
        this.client = HttpClient.newHttpClient();
    }

    public void send(String method) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseURL))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build();

        for (int i = 0; i < LoadTestClient.MAX_RETRIES; i++) {
            try {
                HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                int code = response.statusCode();
                if (code >= 200 && code < 400) {
                    return;
                }
                if (code >= 400 && code < 500) {
                    System.err.println("Client error: " + code);
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        RequestCounter.incrementError();
    }
}

class RequestCounter {
    private static final AtomicInteger totalRequests = new AtomicInteger(0);
    private static final AtomicInteger errorRequests = new AtomicInteger(0);

    public static int getTotalRequests() {
        return totalRequests.get();
    }

    public static int getErrorRequests() {
        return errorRequests.get();
    }

    public static void incrementTotal() {
        totalRequests.incrementAndGet();
    }

    public static void incrementError() {
        errorRequests.incrementAndGet();
    }
}
