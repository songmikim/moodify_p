package xyz.moodf.diary.exceptions;

import org.springframework.http.HttpStatus;
import xyz.moodf.global.exceptions.script.AlertBackException;

public class SentimentNotFoundException extends AlertBackException {
    public SentimentNotFoundException() {
        super("NotFound.sentiment", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}
