package gr.aueb.dmst.monitor;

// Record που υλοποιεί τα DockerInstances και Images. Χρησιμοποιείται για μεγαλύτερη ευκολία στην μοντελοποίησή τους
// Δυνατότητα δημιουργία αντικειμένων χωρίς κατασκευαστή
public record ContainerInstanceMetrics(
    String id,
    String image,
    String name,
    String timestamp,
    double cpuPercent,
    double memoryPercent
) {
    
}

