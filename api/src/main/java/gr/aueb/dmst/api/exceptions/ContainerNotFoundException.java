package gr.aueb.dmst.api.exceptions;

// Exception that occurs when the given id does not match that of a Container

public class ContainerNotFoundException extends RuntimeException {
    public ContainerNotFoundException(String id) {
        super("No such container: " + id);
    }
}
