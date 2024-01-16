package gr.aueb.dmst.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gr.aueb.dmst.api.entities.ContainerInstances;

public interface ContainerInstancesRepository extends JpaRepository<ContainerInstances, String> {
    
}
