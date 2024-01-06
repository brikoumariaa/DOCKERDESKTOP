package gr.aueb.dmst.monitor;

public record ContainerInstanceMetrics(
    String id,
    String image,
    String name,
    String timestamp,
    double cpuPercent,
    double memoryPercent
) {
    
}
