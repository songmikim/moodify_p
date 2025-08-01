package xyz.moodf.diary.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import xyz.moodf.diary.dtos.DiaryRequest;

@Lazy
@Component
@RequiredArgsConstructor
public class DiaryValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(DiaryRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }
        DiaryRequest form = (DiaryRequest) target;

        System.out.println("검증중");

        // title, content, date, weather 비어 있는지 검증
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "NotBlank.diaryRequest.title");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "NotBlank.diaryRequest.content");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "date", "NotNull.diaryRequest.date");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "weather", "NotNull.diaryRequest.weather");
        if (errors.hasErrors()) return;
    }
}
