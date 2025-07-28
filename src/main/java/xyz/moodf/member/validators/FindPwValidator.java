package xyz.moodf.member.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import xyz.moodf.member.controllers.RequestFindPw;
import xyz.moodf.member.repositories.MemberRepository;

@Component
@RequiredArgsConstructor
public class FindPwValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestFindPw.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        //이메일과 회원명 조합으로 조회
        RequestFindPw form = (RequestFindPw) target;
        String email = form.email();
        String name = form.name();

        if (StringUtils.hasText(email) && StringUtils.hasText(name) && !memberRepository.existsByEmailAndName(email, name)) {
            errors.reject("NotFound.member");
        }
    }
}
