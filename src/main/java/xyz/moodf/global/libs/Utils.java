package xyz.moodf.global.libs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jdk.tools.jlink.internal.plugins.PluginsResourceBundle.getMessage;

@Component
@RequiredArgsConstructor
//@EnableConfigurationProperties(FileProperties.class)
public class Utils {

    private final HttpServletRequest request;
    private final MessageSource messageSource;
    //private final FileProperties fileProperties;
    //private final FileInfoService inforservice;


    public int version() {
        return 1;
    }

    // 휴대폰 or PC 확인

    public boolean isMobile() {
        String ua = request.getHeader("User-Agent");

        String pattern = ".*(iPhone|iPod|iPad|BlackBerry|Android|Windows CE|LG|MOT|SAMSUNG|SonyEricsson).*";

        // UserAgent가  패턴과 일치하면 True 반환
        return StringUtils.hasText(ua) && ua.matches(pattern);
    }

    // mobile, front 템플릿 분리
    public String tpl(String path) {
        String prefix = isMobile() ? "mobile" : "front";

        return String.format("%s/%s", prefix, path);
    }

    // 메세지를 코드로 조회
    public List<String> getMessages(String[] codes) {
        // messages.properties 파일 호출
        ResourceBundleMessageSource ms = (ResourceBundleMessageSource) messageSource;
        // 기본 메세지 false
        ms.setUseCodeAsDefaultMessage(false);
        try{
            return Arrays.stream(codes)
                    .map(c -> {
                        try {
                            return getMessage(c);
                        } catch (Exception e) {}
                        return "";
                    }).filter(s -> !s.isBlank()).toList();
        } finally {
            ms.setUseCodeAsDefaultMessage(true);
        }
    }


    // 커맨드 객체 검증 실패 메세지 처리(REST)
    public Map<String, List<String>> getErrorMessages(Errors errors){
        // 필드 별 검증 실패 메세지
        Map<String, List<String>> messages = errors.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, f -> getMessages(f.getCodes()), (v1, v2) -> v2));

        // 글로벌 검증 실패 메세지
        List<String> gMessages = errors.getGlobalErrors()
                .stream()
                .flatMap(g -> getMessages(g.getCodes()).stream()).toList();
        if (!gMessages.isEmpty()) {
            messages.put("global", gMessages);
        }

        return messages;
    }

}
