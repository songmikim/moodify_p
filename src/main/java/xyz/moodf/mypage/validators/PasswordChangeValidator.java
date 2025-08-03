package xyz.moodf.mypage.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import xyz.moodf.global.validators.PasswordValidator;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;
import xyz.moodf.mypage.entities.RequestPasswordChange;

@Lazy
@Component
@RequiredArgsConstructor
public class PasswordChangeValidator implements Validator, PasswordValidator {

    private final MemberUtil memberUtil;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 비교용 인코더

    /**
     * 어떤 클래스 타입의 객체를 검증할 수 있는지 판단하는 메서드
     * RequestPasswordChange 클래스 또는 그 하위 타입 객체만 검증 가능함
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestPasswordChange.class);
    }

    /**
     * 실제 유효성 검사를 수행하는 메서드
     * - 필수 항목 비어있는지 확인
     * - 현재 비밀번호 일치 여부 확인
     * - 새로운 비밀번호의 복잡도 검사
     * - 비밀번호 확인값 일치 여부 확인
     *
     * @param target 검증 대상 객체 (RequestPasswordChange)
     * @param errors 유효성 오류 정보를 담는 객체
     */
    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) return; // 기존 오류가 있으면 추가 검증 중단

        RequestPasswordChange form = (RequestPasswordChange) target;

        // 비어 있는 입력 필드 검증
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", "NotBlank.currentPassword");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotBlank.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotBlank.confirmPassword");
        if (errors.hasErrors()) return; // 필수값 미입력 에러가 있으면 이후 검증 생략

        // 현재 비밀번호가 회원 정보와 일치하는지 확인
        Member member = memberUtil.getMember();
        if (member == null || !passwordEncoder.matches(form.getCurrentPassword(), member.getPassword())) {
            errors.rejectValue("currentPassword", "Mismatch.currentPassword");
            return;
        }

        // 동일 비밀번호 사용 시 에러 메시지를 추가
        if (passwordEncoder.matches(form.getPassword(), member.getPassword())) {
            errors.rejectValue("password", "SameAsCurrent.password");
            return;
        }

        String password = form.getPassword();

        // 새로운 비밀번호 복잡도 체크 (영문자, 숫자, 특수문자 포함 여부)
        if (!checkAlpha(password, false) || !checkNumber(password) || !checkSpecialChars(password)) {
            errors.rejectValue("password", "Complexity.password");
        }

        // 비밀번호 확인값과 일치하는지 검증
        if (!password.equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Mismatch.confirmPassword");
        }
    }
}