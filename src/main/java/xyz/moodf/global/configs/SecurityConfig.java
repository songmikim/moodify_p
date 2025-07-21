package xyz.moodf.global.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /* 인증 설정 - 로그인, 로그아웃 */
        http.formLogin(c -> {
            c.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    ;
        });

        http.logout(c -> {
            c.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login");
        });

        /* 자동 로그인 - RememberMe */
        http.rememberMe(c -> {
            c.rememberMeParameter("autoLogin")
                    .tokenValiditySeconds(60 * 60 * 24 * 30)
                    ;
        });

        /* 인가 설정 - 자원에 대한 접근 권한 */
        http.authorizeHttpRequests(c -> {
            c.requestMatchers("/login", "/join", "/notice").anonymous() // 비회원 전용
                    .requestMatchers("/mypage/**").authenticated() // 회원 전용
                    .anyRequest().permitAll();
        });

        http.exceptionHandling(c -> {

        });


        http.headers(c -> c.frameOptions(f -> f.sameOrigin()));

        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
