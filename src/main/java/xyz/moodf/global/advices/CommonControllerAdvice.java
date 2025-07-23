package xyz.moodf.global.advices;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.exceptions.CommonException;
import xyz.moodf.global.exceptions.script.AlertBackException;
import xyz.moodf.global.exceptions.script.AlertException;
import xyz.moodf.global.exceptions.script.AlertRedirectException;
import xyz.moodf.global.libs.Utils;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@ControllerAdvice(annotations = ApplyCommonController.class)
public class CommonControllerAdvice {
    private final Utils utils;

    @ExceptionHandler(Exception.class)
    public ModelAndView errorHandler(Exception e, HttpServletRequest request) {

        Map<String, String> data = new HashMap<>();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        String message = e.getMessage();
        String tpl = "error/common";

        if (e instanceof CommonException commonException) {
            status = commonException.getStatus();
            if (commonException.isErrorCode()) { // 메세지 코드로 메세지를 가져와야 하는 경우
                message = utils.getMessage(message);
            }

            // 자바스크립트 alert 형태로 출력하는 예외
            if (e instanceof AlertException) {
                tpl = "common/_execute_script";
                String callback = "";

                // history.back() 추가
                if (e instanceof AlertBackException alertBackException) {
                    callback = String.format("() => %s.history.back()", alertBackException.getTarget());
                }

                // location.replace(..) 추가
                if (e instanceof AlertRedirectException redirectException) {
                    callback = String.format("() => %s.location.replace('%s')", redirectException.getTarget(), request.getContextPath() + redirectException.getUrl());
                }

                String script = String.format("alert('%s', %s);", message, callback);
                data.put("script", script);
            }

        }

        data.put("status", status.toString());
        data.put("message", message);
        data.put("path", request.getRequestURI());
        data.put("method", request.getMethod());

        ModelAndView mv = new ModelAndView();
        mv.setStatus(status);
        mv.addAllObjects(data);
        mv.setViewName(tpl);

        e.printStackTrace();

        return mv;
    }
}
