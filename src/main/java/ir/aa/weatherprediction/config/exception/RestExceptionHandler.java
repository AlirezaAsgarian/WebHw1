package ir.aa.weatherprediction.config.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleAccessDeniedException(ResponseStatusException ex) {
        // Customize the response message or any additional data you want to include
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getBody());
    }

}

