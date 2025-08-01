package xyz.moodf.diary.exceptions;

import org.springframework.http.HttpStatus;
import xyz.moodf.global.exceptions.script.AlertBackException;

public class DiaryNotFoundException extends AlertBackException {
    public DiaryNotFoundException() {
        super("NotFound.diary", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}
