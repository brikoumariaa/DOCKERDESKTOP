package gr.aueb.dmst.api.exceptions;

public class ContainerNotFoundException extends RuntimeException {
    public ContainerNotFoundException(String id) {
        super("No such container: " + id);
    }
}
