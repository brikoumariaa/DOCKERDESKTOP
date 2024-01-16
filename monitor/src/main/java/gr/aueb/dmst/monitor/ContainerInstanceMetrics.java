package gr.aueb.dmst.monitor;

// Record implemeting DockerInstances and Images. Used for greater ease in their modeling
// Ability to create objects without a constructor
public record ContainerInstanceMetrics(
    String id,
    String image,
    String name,
    String timestamp,
    double cpuPercent,
    double memoryPercent
) {
    
}

