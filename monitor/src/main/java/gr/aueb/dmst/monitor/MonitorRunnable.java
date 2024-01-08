package gr.aueb.dmst.monitor;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
// η κλαση υλοποιεί τη διεπαφή runnable ώστε να μπορεί να εκτελεστεί από ξεχωριστό Thread
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


    // Μέθοδος run που κάνει fetch πληροφορίες για τα Containers
    @Override
    public void run() {
        while (true) {
            try {
                List<Container> containers = dockerClient
                        .listContainersCmd()
                        .withShowAll(false)
                        .exec();
                
                // Παίρνουμε τις μετρικές του κάθε container από την λίστα
                for (Container container : containers) {
                    String containerId = container.getId();
                    String image = container.getImage();
                    String name = container.getNames()[0].substring(1);

                    // Υπολογίζουμε CPU και memory ποσοστά και τα βάζουμε στην queue στο try block καλώντας τις αντιστοιχες μεθόδους                 
                    dockerClient.statsCmd(containerId)
                        .withNoStream(true)
                        .exec(new ResultCallback.Adapter<Statistics>() {
                            @Override
                            public void onNext(Statistics stats) {
                                String timestamp = stats.getRead();
                                double cpuPercent = computeCpuPercent(stats);
                                double memoryPercent = computeMemoryPercent(stats);

                                try {
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
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    // Υπολογίζει CPU ποσοστά με βάση statistcs
    public double computeCpuPercent(Statistics stats) {
        long cpuDelta = stats.getCpuStats().getCpuUsage().getTotalUsage() - stats.getPreCpuStats().getCpuUsage().getTotalUsage();
        long systemDelta = stats.getCpuStats().getSystemCpuUsage() - stats.getPreCpuStats().getSystemCpuUsage();
        int numberCores = stats.getCpuStats().getCpuUsage().getPercpuUsage().size();
        return (((double) cpuDelta / systemDelta) * numberCores) * 100;
    }

    // // Υπολογίζει memory ποσοστά με βάση statistcs
    public double computeMemoryPercent(Statistics stats) {
        long memoryUsage = stats.getMemoryStats().getUsage();
        long memoryLimit = stats.getMemoryStats().getLimit();
        return ((double) memoryUsage / memoryLimit) * 100;
    }
}
