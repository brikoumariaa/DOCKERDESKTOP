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
// αντι για images και instances. Δημιουργεί αντικείμενο containers. Το καναμε για ευκολότερη μοντελοποίηση και υπάρχουν στο ΓΚΙΤ
