package xyz.moodf.global.file.exceptions;

import xyz.moodf.global.exceptions.NotFoundException;

public class FileNotFoundException extends NotFoundException {
    public FileNotFoundException() {
        super("NotFound.file");
        setErrorCode(true);
    }
}
