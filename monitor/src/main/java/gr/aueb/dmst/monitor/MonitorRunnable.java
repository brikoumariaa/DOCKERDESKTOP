package gr.aueb.dmst.monitor;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;

public class MonitorRunnable implements Runnable {
    private final DockerClient dockerClient;
    private final BlockingQueue<ContainerInstanceMetrics> queue;

    public MonitorRunnable(
        DockerClient dockerClient,
        BlockingQueue<ContainerInstanceMetrics> queue
    ) {
        this.dockerClient = dockerClient;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // List all Docker containers
                List<Container> containers = dockerClient
                        .listContainersCmd()
                        .withShowAll(false)
                        .exec();
                // Iterate over each container
                for (Container container : containers) {
                    String containerId = container.getId();
                    String image = container.getImage();
                    String name = container.getNames()[0].substring(1);

                    // Retrieve container statistics
                    dockerClient.statsCmd(containerId)
                        .withNoStream(true)
                        .exec(new ResultCallback.Adapter<Statistics>() {
                            @Override
                            public void onNext(Statistics stats) {
                                String timestamp = stats.getRead();
                                double cpuPercent = computeCpuPercent(stats);
                                double memoryPercent = computeMemoryPercent(stats);

                                try {
                                    // Put metrics into the blocking queue
                                    queue.put(
                                        new ContainerInstanceMetrics(
                                            containerId,
                                            image,
                                            name,
                                            timestamp,
                                            cpuPercent,
                                            memoryPercent
                                        )
                                    );
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    e.printStackTrace();
                                }
                            }
                        });
                }
                // Sleep for 10 seconds before the next iteration
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
    
    // Compute CPU percentage based on container statistics
    public double computeCpuPercent(Statistics stats) {
        long cpuDelta = stats.getCpuStats().getCpuUsage().getTotalUsage() - stats.getPreCpuStats().getCpuUsage().getTotalUsage();
        long systemDelta = stats.getCpuStats().getSystemCpuUsage() - stats.getPreCpuStats().getSystemCpuUsage();
        int numberCores = stats.getCpuStats().getCpuUsage().getPercpuUsage().size();
        return (((double) cpuDelta / systemDelta) * numberCores) * 100;
    }
    // Compute memory percentage based on container statistics
    public double computeMemoryPercent(Statistics stats) {
        long memoryUsage = stats.getMemoryStats().getUsage();
        long memoryLimit = stats.getMemoryStats().getLimit();
        return ((double) memoryUsage / memoryLimit) * 100;
    }
}
