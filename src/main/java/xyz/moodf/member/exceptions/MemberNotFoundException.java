package xyz.moodf.member.exceptions;

import xyz.moodf.global.exceptions.NotFoundException;
public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException() {
        super("NotFound.member");
        setErrorCode(true);
    }
}
