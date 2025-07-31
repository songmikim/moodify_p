package xyz.moodf.admin.board.exceptions;

import org.springframework.http.HttpStatus;
import xyz.moodf.global.exceptions.script.AlertBackException;

public class BoardDataNotFoundException extends AlertBackException {
    public BoardDataNotFoundException() {
        super("NotFound.boardData", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}
