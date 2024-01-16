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

    public ContainerController(
        DockerClient dockerClient,
        ContainerMetricsRepository containerMetricsRepository
    ) {
        this.dockerClient = dockerClient;
        this.containerMetricsRepository = containerMetricsRepository;
    }

    private record ContainerResponse (
        String id,
        String image,
        String name,
        String status
    ) {}

    @GetMapping("/containers")
    public List<ContainerResponse> dockerPs(@RequestParam boolean all) {
        List<ContainerResponse> response = new ArrayList<>();
        List<Container> containers = dockerClient
                .listContainersCmd()
                .withShowAll(all)
                .exec();

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

    private enum DockerCommand {
        START,
        STOP
    }

    @GetMapping("/containers/{id}")
    public void dockerCommand(
        @PathVariable String id, @RequestParam DockerCommand command
    ) {
        try {
            if (command.equals(DockerCommand.START)) {
                dockerClient.startContainerCmd(id).exec();
            } else {
                dockerClient.stopContainerCmd(id).exec();
            }
        } catch (NotFoundException e) {
            throw new ContainerNotFoundException(id);
        }
    }

    @GetMapping("/containers/{id}/metrics")
    public List<ContainerMetrics> getContainerMetrics(
        @PathVariable String id,
        @RequestParam Timestamp start,
        @RequestParam Timestamp end
    ) {
        return containerMetricsRepository.findAllByContainerInstances_IdStartingWithAndMeasurementTimestampBetween(id, start, end);
    }
}
