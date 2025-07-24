package xyz.moodf.member.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import xyz.moodf.global.validators.RedirectUrlValidator;
import xyz.moodf.member.controllers.RequestLogin;

import java.io.IOException;
import java.util.Objects;

public class LoginSuccessHandler implements AuthenticationSuccessHandler, RedirectUrlValidator {
    /**
     * Authentication authentication
     * - 인증 정보가 담겨 있는 객체
     *
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("결과: 성공");
        HttpSession session = request.getSession();
        RequestLogin form = (RequestLogin) session.getAttribute("requestLogin");
        form = Objects.requireNonNullElseGet(form, RequestLogin::new);

        String redirectUrl = form.getRedirectUrl();

        redirectUrl = getAllowedRedirectUrl(redirectUrl);

        System.out.println("redirectUrl: "+ redirectUrl);

        session.removeAttribute("requestLogin");

        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}
