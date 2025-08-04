package xyz.moodf.global.libs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.LocaleResolver;
import xyz.moodf.global.configs.FileProperties;
import xyz.moodf.global.file.entities.FileInfo;
import xyz.moodf.global.file.services.FileInfoService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class Utils {

    private final HttpServletRequest request;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final FileInfoService fileInfoService;

    public int version() {
        return 1;
    }

    public String keywords() {
        return "";
    }

    public String description() {
        return "";
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
        //String prefix = isMobile() ? "mobile" : "front";
        String prefix = "front";

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

    public String printThumb(String gid, int width, int height) {
        FileInfo item = fileInfoService.get(gid);
        if (item == null) return "";
        return printThumb(item.getSeq(), width, height);
    }

    public String printNoImage(){
        String url= request.getContextPath() + "/common/images/no_image.jpg";

        return String.format("<img src='%s'>",url);

    }

    public String getUrl(String url){
        String protocol = request.getScheme(); // http, https,ftp ....
        String domain = request.getServerName();
        int _port = request.getServerPort();
        String port = List.of(80, 443).contains(_port) ? "":":"+_port;

        return String.format("%s://%s%s%s%s", protocol, domain, port, request.getContextPath(), url);
    }

    // 알파벳, 숫자, 특수문자 조합 랜덤 문자열 생성
    public String randomChars() {
        return randomChars(8);
    }

    public String randomChars(int length) {
        // 알파벳 생성
        Stream<String> alphas = IntStream.concat(IntStream.rangeClosed((int)'a', (int)'z'), IntStream.rangeClosed((int)'A', (int)'Z')).mapToObj(s -> String.valueOf((char)s));

        // 숫자 생성
        Stream<String> nums = IntStream.range(0, 10).mapToObj(String::valueOf);

        // 특수문자 생성 나중에 너무 힘든건 빼던가
        Stream<String> specials = Stream.of("~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "=", "[", "{", "}", "]", ";", ":");

        List<String> chars = Stream.concat(Stream.concat(alphas, nums), specials).collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(chars);

        return chars.stream().limit(length).collect(Collectors.joining());
    }
}
