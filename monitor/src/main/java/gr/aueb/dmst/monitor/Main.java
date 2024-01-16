package gr.aueb.dmst.monitor;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

public class Main {
    public static void main(String[] args) {

        DockerClient dockerClient = createDockerClient();
        BlockingQueue<ContainerInstanceMetrics> queue = new LinkedBlockingQueue<>();
        Database db = new Database();

        MonitorRunnable monitorRunnable = new MonitorRunnable(dockerClient, queue);
        DatabaseRunnable databaseRunnable = new DatabaseRunnable(queue, db);

        Thread monitorThread = new Thread(monitorRunnable, "Monitor");
        Thread databaseThread = new Thread(databaseRunnable, "Database");

        monitorThread.start();
        databaseThread.start();
    }

    public static DockerClient createDockerClient() {
        DockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375")
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}
