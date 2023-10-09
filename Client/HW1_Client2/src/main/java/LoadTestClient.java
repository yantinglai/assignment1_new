import java.io.*;
import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LoadTestClient {

    private final String baseUrl;
    private final Queue<RequestRecord> records = new ConcurrentLinkedQueue<>();

    public LoadTestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void startLoadTest(int numThreads, int numRequests) throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(numThreads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            tasks.add(() -> {
                sendRequests(numRequests);
                return null;
            });
        }

        service.invokeAll(tasks);
        service.shutdown();
        service.awaitTermination(1, TimeUnit.HOURS);

        saveRecordsToFile("output.csv");
        displayStats();
    }

    private void sendRequests(int numRequests) {
        for (int i = 0; i < numRequests; i++) {
            sendRequest("GET");
            sendRequest("POST");
        }
    }

    private void sendRequest(String method) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(10))
                .build();

        long startTime = System.currentTimeMillis();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            long endTime = System.currentTimeMillis();
            records.add(new RequestRecord(startTime, method, (endTime - startTime), response.statusCode()));
        } catch (IOException | InterruptedException ex) {
            // Handle error appropriately.
        }
    }

    private void saveRecordsToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (RequestRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayStats() {
        List<Long> getLatencies = records.stream().filter(r -> "GET".equals(r.method)).map(r -> r.latency).sorted().collect(Collectors.toList());
        List<Long> postLatencies = records.stream().filter(r -> "POST".equals(r.method)).map(r -> r.latency).sorted().collect(Collectors.toList());

        System.out.println("GET Requests:");
        displayStatsForMethod(getLatencies);
        System.out.println("\nPOST Requests:");
        displayStatsForMethod(postLatencies);
    }

    private void displayStatsForMethod(List<Long> latencies) {
        double mean = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double median = latencies.get(latencies.size() / 2);
        double p99 = latencies.get((int) (latencies.size() * 0.99));
        long min = latencies.get(0);
        long max = latencies.get(latencies.size() - 1);

        System.out.println("Mean Response Time: " + mean + " ms");
        System.out.println("Median Response Time: " + median + " ms");
        System.out.println("99%: " + p99 + " ms");
        System.out.println("Max Response Time: " + max + " ms");
        System.out.println("Min Response Time: " + min + " ms");
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        if (args.length != 4) {
            System.out.println("Usage: java LoadTestClient <numberOfThreads> <numberOfRequests> <latency> <url>");
            return;
        }

        int numThreads = Integer.parseInt(args[0]);
        int numRequests = Integer.parseInt(args[1]);
        int latency = Integer.parseInt(args[2]); // in seconds
        String baseUrl = args[3];

        LoadTestClient client = new LoadTestClient(baseUrl);
        client.startLoadTest(numThreads, numRequests);
    }

}

class RequestRecord {
    long startTime;
    String method;
    long latency;
    int statusCode;

    public RequestRecord(long startTime, String method, long latency, int statusCode) {
        this.startTime = startTime;
        this.method = method;
        this.latency = latency;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return startTime + "," + method + "," + latency + "," + statusCode;
    }
}
