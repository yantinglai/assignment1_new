## Client1: 
LoadTestClient is a Java-based load testing tool designed to simulate real-world usage of a web server by sending multiple HTTP requests in quick succession.
# Configuration: To configure and run the LoadTestClient, use the following command-line arguments:
threadGroupSize: Number of threads per group.
numThreadGroups: Number of thread groups.
delay: Delay between thread groups (in seconds).
baseURL: Target server's base URL.
Bootstrapping Phase: In this warm-up phase, the LoadTestClient initializes the test by sending a predefined number of requests using a set number of threads to prepare the server for the main load test.
Benchmark Phase: LoadTestClient1 creates thread groups, each with a defined number of threads, and sends HTTP requests to the target server. It waits for a specified delay between groups.
# Metrics:
At the end of the test, the LoadTestClient outputs:
Elapsed Time: Total time taken for the test (in seconds).
Total Request Count: The aggregate number of requests sent during the test.
Request Rate: The average rate of requests sent, measured in requests per second.
# Key Classes:
LoadTestClient: This is the main class, responsible for orchestrating the entire load test process based on the provided configurations.
RequestTask: This class implements Runnable and represents the worker threads that send the HTTP requests.
WebAPI: This is the HTTP client utility class. It sends HTTP requests to the specified baseURL and handles any necessary retries based on response codes or errors.
RequestCounter: A utility class to track and report the total number of requests and the number of error requests.

## Client2:  Enhanced Load Testing Framework for Web Servers
# Packages and Classes:
LoadTestClient: The central class that orchestrates the entire load testing process. It sets up threads, triggers test phases, saves results to files, and calculates statistics about API call latencies.
RequestRecord: A data structure that captures essential information about each API call, such as start time, HTTP method, latency, and status code.
# Relationships:
LoadTestClient manages and conducts all API interactions, which includes initializing threads, triggering API calls, and collecting results.
RequestRecord objects are created by the LoadTestClient for every API request made and are stored in a concurrent queue for post-test analysis.
How the Client2  Works:
# Bootstrapping Phase:
LoadTestClient starts by initializing an ExecutorService to set up a fixed pool of threads.
Each thread is tasked with making a predetermined number of GET and POST API calls.
The details of every request are logged as RequestRecord entries in a shared concurrent queue (records).
# Test Phase:
Each thread (under the LoadTestClient) makes both GET and POST API calls.
The details of these requests are again captured and stored in the records queue.
# Logging and Statistics:
Once the test phase is over, LoadTestClient logs the records queue to an output file named output.csv.
Latency statistics, such as mean, median, 99th percentile, maximum, and minimum latencies, are computed and displayed for both GET and POST requests.

## What has changed from Client 1 to Client 2?
Optimized Resource Utilization: In Client2, a new HttpClient is created for every request. This design can be optimized further in future versions.
Concurrent Execution: Client2 uses a fixed thread pool for concurrent execution of requests, enabling higher throughput.
Explicit Timeout Setting: Client2 introduces an explicit timeout setting for requests, ensuring they don't hang indefinitely.
Enhanced Logging and Analysis: In Client2, after all requests are made, latency statistics are calculated and displayed for a comprehensive understanding of the system's performance.
