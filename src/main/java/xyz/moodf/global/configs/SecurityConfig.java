package xyz.moodf.global.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import xyz.moodf.member.services.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final MemberInfoService infoService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /* 인증 설정 - 로그인, 로그아웃 */
        http.formLogin(c -> {
            c.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .successHandler(new LoginSuccessHandler())
                    .failureHandler(new LoginFailureHandler());
        });

        http.logout(c -> {
            c.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login");
        });

        /* 자동 로그인 - RememberMe */
        http.rememberMe(c -> {
            c.rememberMeParameter("autoLogin")
                    .tokenValiditySeconds(60 * 60 * 24 * 30)
                    .userDetailsService(infoService)
                    .authenticationSuccessHandler(new LoginSuccessHandler());
        });

        /* 인가 설정 - 자원에 대한 접근 권한 설정 */
        /**
         * authenticated() : 인증 받은 사용자만 접근 가능 (회원)
         * anonymous() : 인증 받지 않은 사용자만 접근 가능 (비회원)
         * permitAll() : 모든 사용자가 접근 가능
         * hasAuthority("권한이름") : 하나의 권한을 체크
         * hasAnyAuthority("권한1", "권한2", ...) : 다수의 권한을 체크
         * hasRole("롤이름") : ROLE_롤이름, 롤 이름으로 권한을 체크
         * hasAnyRole("롤1", "롤2", ...) : 다수의 롤(역할)으로 권한을 체크
         * anyRequest().permitAll() : 비회원 페이지가 기본, 일부 페이지 -> 회원 전용 사이트
         * anyRequest().authenticated() : 회원 전용 페이지가 기본, 일부 페이지 -> 비회원 사이트
         */
        http.authorizeHttpRequests(c -> {
            c.requestMatchers("/login", "/join", "/board/**", "/diary/**", "/error/**").permitAll()
                    .requestMatchers("/front/**", "/mobile/**", "/member/**", "/common/**").permitAll()
                    .requestMatchers("/admin/**").hasAuthority("ADMIN")
                    .requestMatchers("/admin/**").permitAll()
                   .anyRequest().authenticated();

        });

        http.exceptionHandling(c -> {
            c.authenticationEntryPoint(new MemberAuthenticationExceptionHandler()); // 미로그인 상태에서의 인가 실패에 대한 처리
            c.accessDeniedHandler(new MemberAccessDeniedHandler()); // 인증 받은 회원이 권한이 없는 페이지에 접근한 경우
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
