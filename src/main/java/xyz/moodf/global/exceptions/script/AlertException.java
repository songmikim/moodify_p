package xyz.moodf.global.exceptions.script;

import org.springframework.http.HttpStatus;
import xyz.moodf.global.exceptions.CommonException;

public class AlertException extends CommonException {
    public AlertException(String message, HttpStatus status) {
        super(message, status);
    }

    public AlertException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }
}
