package xyz.moodf.global.validators;

import org.springframework.util.StringUtils;

import java.util.List;

public interface RedirectUrlValidator {
    default String getAllowedRedirectUrl(String redirectUrl) {
        List<String> ALLOWED_REDIRECT_PREFIXES = List.of(
                "/diary", "/mypage", "/admin", "/board", "/member"
        );

        if (!StringUtils.hasText(redirectUrl) || ALLOWED_REDIRECT_PREFIXES.stream().noneMatch(redirectUrl::startsWith)) {
            return "/diary";  // 기본 fallback
        }

        return redirectUrl;
    }
}
