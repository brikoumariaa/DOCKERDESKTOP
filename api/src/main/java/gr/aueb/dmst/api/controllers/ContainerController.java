package gr.aueb.dmst.api.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;

import gr.aueb.dmst.api.entities.ContainerMetrics;
import gr.aueb.dmst.api.exceptions.ContainerNotFoundException;
import gr.aueb.dmst.api.repositories.ContainerMetricsRepository;

@RestController
public class ContainerController {
    private final DockerClient dockerClient;
    private final ContainerMetricsRepository containerMetricsRepository;
    //Constructor for ContainerController
    public ContainerController(
        DockerClient dockerClient,
        ContainerMetricsRepository containerMetricsRepository
    ) {
        this.dockerClient = dockerClient;
        this.containerMetricsRepository = containerMetricsRepository;
    }
    //Record representing the response structure for container information
    private record ContainerResponse (
        String id,
        String image,
        String name,
        String status
    ) {}
    // Endpoint to get a list of containers
    @GetMapping("/containers")
    public List<ContainerResponse> dockerPs(@RequestParam boolean all) {
        List<ContainerResponse> response = new ArrayList<>();
        List<Container> containers = dockerClient
                .listContainersCmd()
                .withShowAll(all)
                .exec();
        // Mapping Docker containers to a custom response structure
        containers.forEach(c -> response.add(
            new ContainerResponse(
                c.getId(),
                c.getImage(),
                c.getNames()[0].substring(1),
                c.getStatus()
            )
        ));
        return response;
    }
    // Enum for Docker commands (START, STOP)
    private enum DockerCommand {
        START,
        STOP
    }
    // Endpoint to execute Docker commands on a specific container
    @GetMapping("/containers/{id}")
    public void dockerCommand(
        @PathVariable String id, @RequestParam DockerCommand command
    ) {
        try {
            //Executing Docker start or stop command based on the input
            if (command.equals(DockerCommand.START)) {
                dockerClient.startContainerCmd(id).exec();
            } else {
                dockerClient.stopContainerCmd(id).exec();
            }
        } catch (NotFoundException e) {
            // Exception in case the container is not found
            throw new ContainerNotFoundException(id);
        }
    }
    // Endpoint to get metrics for specific container within a time range
    @GetMapping("/containers/{id}/metrics")
    public List<ContainerMetrics> getContainerMetrics(
        @PathVariable String id,
        @RequestParam Timestamp start,
        @RequestParam Timestamp end
    ) {
        // Querying container metrics from the repository based on the container ID and time range
        return containerMetricsRepository.findAllByContainerInstances_IdStartingWithAndMeasurementTimestampBetween(id, start, end);
    }
}
