package gr.aueb.dmst.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ContainerInstances {
    @Id
    private String id;

    private String image;

    private String name;

    public ContainerInstances() {}

    public ContainerInstances(String id, String image, String name) {
        this.id = id;
        this.image = image;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ContainerInstance [id=" + id + ", image=" + image + ", name=" + name + "]";
    }
}
