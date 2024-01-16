package gr.aueb.dmst.api.repositories;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gr.aueb.dmst.api.entities.ContainerMetrics;

// A repository for ContainerMetrics

public interface ContainerMetricsRepository extends JpaRepository<ContainerMetrics, Long> {
    List<ContainerMetrics> findAllByContainerInstances_IdStartingWithAndMeasurementTimestampBetween(
        String containerId,
        Timestamp startTime,
        Timestamp endTime
    );
}
