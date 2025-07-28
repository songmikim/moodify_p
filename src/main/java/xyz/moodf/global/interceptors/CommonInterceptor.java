    package xyz.moodf.global.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import xyz.moodf.member.libs.MemberUtil;

@Component
@RequiredArgsConstructor
public class CommonInterceptor implements HandlerInterceptor {
    private final MemberUtil memberUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        clearSocialToken(request);

        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("isLogin", memberUtil.isLogin());
            modelAndView.addObject("isAdmin", memberUtil.isAdmin());
            modelAndView.addObject("LoggedMember", memberUtil.getMember());
        }
    } // 로그인 회원정보 유지


        private void clearSocialToken(HttpServletRequest request) {
            String url = request.getRequestURI();
            if (!url.contains("/join")) {
                HttpSession session = request.getSession();
                session.removeAttribute("socialType");
                session.removeAttribute("socialToken");
            }
        }

    }
