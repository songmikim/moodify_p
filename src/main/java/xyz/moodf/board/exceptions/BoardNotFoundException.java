package xyz.moodf.board.exceptions;

import org.springframework.http.HttpStatus;
import xyz.moodf.global.exceptions.script.AlertBackException;

public class BoardNotFoundException extends AlertBackException {
  public BoardNotFoundException() {
    super("NotFound.board", HttpStatus.NOT_FOUND);
    setErrorCode(true);
  }
}
