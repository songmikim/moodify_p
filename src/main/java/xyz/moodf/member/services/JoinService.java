package xyz.moodf.member.services;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.moodf.global.file.services.FileUploadService;
import xyz.moodf.member.constants.Authority;
import xyz.moodf.member.controllers.RequestJoin;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.repositories.MemberRepository;
import xyz.moodf.member.social.constants.SocialType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

@Lazy
@Service
@RequiredArgsConstructor
public class JoinService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final MemberRepository repository;
    private final FileUploadService uploadService;
    private final HttpSession session;

    public void process(RequestJoin form) {
        /**
         * 1. 비밀번호를 BCrypt 해시화
         * 2. 휴대전화번호 통일화, 010-1000-1000, 01010001000, 010.1000.1000
         *      - 숫자만 남기고 다 제거
         * 3. DB에 영구 저장
         */
        String password = form.getPassword();
        String hash = StringUtils.hasText(password) ? encoder.encode(password) : null;

        String mobile = form.getMobile();
        if (StringUtils.hasText(mobile)) {
            mobile = mobile.replaceAll("\\D", "");
        }

        Member member = modelMapper.map(form, Member.class);

        String pattern = "^a.*";
        if (Pattern.matches(pattern, member.getName())) {
            member.setAuthority(Authority.ADMIN);
        }

        member.setPassword(hash);
        member.setMobile(mobile);
        member.setCredentialChangedAt(LocalDateTime.now());
        member.setSocialType(Objects.requireNonNullElse(form.getSocialType(), SocialType.NONE));
        member.setSocialToken(form.getSocialToken());

        String gid = form.getGid();
        member.setGid(gid);
        repository.saveAndFlush(member);

        // 파일 업로드 완료 처리
        uploadService.processDone(gid);

        // 소셜 로그인 관련 세션값 삭제
        session.removeAttribute("socialType");
        session.removeAttribute("socialToken");
    }
}
