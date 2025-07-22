package xyz.moodf.global.libs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
//@EnableConfigurationProperties(FileProperties.class)
public class Utils {

    private final HttpServletRequest request;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
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

    public String getMessage(String code) {
        Locale locale = localeResolver.resolveLocale(request);

        return messageSource.getMessage(code, null, locale);
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

    public String getParam(String name) {
        return request.getParameter(name);
    }

    /**
     * Thumbnail 이미지를 템플릿에서 출력하는 함수
     *
     * @param seq 파일 번호..?
     * @param width 이미지 너비
     * @param height 이미지 높이
     * @param addClass img 태그에 추가할 클래스 이름
     * @param crop 크롭 여부
     * @return
     */
    public String printThumb(Long seq, int width, int height, String addClass, boolean crop) {
        String url = null;
        try {
//            FileInfo item = infoService.get(seq);
            long folder = seq % 10L;
            url = String.format("%s/file/thumb?seq=%s&width=%s&height=%s&crop=true", request.getContextPath(), seq, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        url = StringUtils.hasText(url) ? url : request.getContextPath() + "/common/images/no_image.jpg";

        return String.format("<img src='%s' class='%s%s'>", url, "image-" + seq, StringUtils.hasText(addClass) ? " " + addClass : "");
    }

    /**
     * Thumbnail 이미지를 템플릿에서 출력하는 함수
     * - crop=true
     *
     * @param seq
     * @param width
     * @param height
     * @param addClass
     * @return
     */
    public String printThumb(Long seq, int width, int height, String addClass) {
        return printThumb(seq, width, height, addClass, true);
    }

    /**
     c
     *
     * @param seq
     * @param width
     * @param height
     * @return
     */
    private String printThumb(Long seq, int width, int height) {
        return printThumb(seq, width, height, null);
    }

    /**
     * Thumbnail 이미지를 템플릿에서 출력하는 함수
     * - crop=true
     * - addClass=null
     * - width=100
     * - height=100
     *
     * @param seq
     * @return
     */
    public String printThumb(Long seq) {
        return printThumb(seq, 100, 100);
    }

    public String printNoImage(){
        String url= request.getContextPath() + "/common/images/no_image,jpg";

        return String.format("<img src='%s'>",url);

    }

    public String getUrl(String url){
        String protocol = request.getScheme(); // http, https,ftp ....
        String domain = request.getServerName();
        int _port = request.getServerPort();
        String port = List.of(80, 443).contains(_port) ? "":":"+_port;

        return String.format("%s://%s%s%s%s", protocol, domain, port, request.getContextPath(), url);
    }
}
