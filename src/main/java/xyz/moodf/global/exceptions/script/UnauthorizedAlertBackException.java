package xyz.moodf.global.exceptions.script;

import org.springframework.http.HttpStatus;

public class UnauthorizedAlertBackException extends AlertBackException {

    /**
     * 기본 타겟 self로 설정
     */
    public UnauthorizedAlertBackException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "self");
    }

    /**
     * 타겟을 지정하는 생성자
     */
    public UnauthorizedAlertBackException(String message, String target) {
        super(message, HttpStatus.UNAUTHORIZED, target);
    }
}