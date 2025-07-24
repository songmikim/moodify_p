package xyz.moodf.mypage.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import xyz.moodf.global.validators.PasswordValidator;
import xyz.moodf.mypage.entities.RequestPasswordChange;

@Component
@RequiredArgsConstructor
public class PasswordChangeValidator implements Validator, PasswordValidator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestPasswordChange.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) return;

        RequestPasswordChange form = (RequestPasswordChange) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotBlank.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotBlank.confirmPassword");

        if (errors.hasErrors()) return;

        String password = form.getPassword();
        if (!checkAlpha(password, false) || !checkNumber(password) || !checkSpecialChars(password)) {
            errors.rejectValue("password", "Complexity.requestJoin.password");
        }

        if (!password.equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Mismatch.confirmPassword");
        }
    }
}