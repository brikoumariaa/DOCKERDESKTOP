package gr.aueb.dmst.api.entities;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ContainerMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "measurement_timestamp", nullable = false)
    private Timestamp measurementTimestamp;

    @Column(name = "cpu_percent", nullable = false)
    private Double cpuPercent;

    @Column(name = "memory_percent", nullable = false)
    private Double memoryPercent;

    @ManyToOne
    @JoinColumn(name = "container_instance_id", nullable = false, referencedColumnName = "id")
    private ContainerInstances containerInstances;

    public ContainerMetrics() {}

    public ContainerMetrics(
        Long id,
        Timestamp measurementTimestamp,
        Double cpuPercent,
        Double memoryPercent,
        ContainerInstances containerInstances
    ) {
        this.id = id;
        this.measurementTimestamp = measurementTimestamp;
        this.cpuPercent = cpuPercent;
        this.memoryPercent = memoryPercent;
        this.containerInstances = containerInstances;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getMeasurementTimestamp() {
        return measurementTimestamp;
    }

    public void setMeasurementTimestamp(Timestamp measurementTimestamp) {
        this.measurementTimestamp = measurementTimestamp;
    }

    public Double getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(Double cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public Double getMemoryPercent() {
        return memoryPercent;
    }

    public void setMemoryPercent(Double memoryPercent) {
        this.memoryPercent = memoryPercent;
    }

    public ContainerInstances getContainerInstance() {
        return containerInstances;
    }

    public void setContainerInstance(ContainerInstances containerInstance) {
        this.containerInstances = containerInstance;
    }

    @Override
    public String toString() {
        return "ContainerMetrics [id=" + id + ", measurementTimestamp=" + measurementTimestamp + ", cpuPercent="
                + cpuPercent + ", memoryPercent=" + memoryPercent + ", containerInstance=" + containerInstances + "]";
    }
}
