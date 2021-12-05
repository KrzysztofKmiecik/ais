package pl.kmiecik.ais.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
class GlobalExceptionHandler {

    @ExceptionHandler({BindException.class})
    public ResponseEntity<Object> catchInputValidationException(BindException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("timeStamp", new Date());

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " - " + error.getDefaultMessage())
                .collect(Collectors.toList());
        body.put("errors", errors);
        log.warn(body.toString());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> catchInputSQLValidationException(Exception ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("timeStamp", new Date());

        String errors = ex.getMessage();
        body.put("errors", errors);
        log.warn(body.toString());
        return new ResponseEntity<>(body, status);
    }

}
