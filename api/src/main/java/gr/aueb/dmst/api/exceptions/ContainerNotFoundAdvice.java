package gr.aueb.dmst.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ContainerNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(ContainerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String containerNotFoundHandler(ContainerNotFoundException ex) {
        return ex.getMessage();
    }
}
