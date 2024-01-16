package gr.aueb.dmst.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gr.aueb.dmst.api.entities.ContainerInstances;

// A repository for every ContainerInstance

public interface ContainerInstancesRepository extends JpaRepository<ContainerInstances, String> {
    
}
