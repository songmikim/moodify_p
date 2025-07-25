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
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestPasswordChange.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) return;

        RequestPasswordChange form = (RequestPasswordChange) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", "NotBlank.currentPassword");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotBlank");

        if (errors.hasErrors()) return;

        Member member = memberUtil.getMember();
        if (member == null || !passwordEncoder.matches(form.getCurrentPassword(), member.getPassword())) {
            errors.rejectValue("currentPassword", "Mismatch.currentPassword");
            return;
        }

        String password = form.getPassword();
        if (!checkAlpha(password, false) || !checkNumber(password) || !checkSpecialChars(password)) {
            errors.rejectValue("password", "Complexity");
        }

        if (!password.equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Mismatch");
        }
    }
}