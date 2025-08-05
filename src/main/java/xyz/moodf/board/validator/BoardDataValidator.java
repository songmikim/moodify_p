package xyz.moodf.board.validator;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import xyz.moodf.admin.board.repositories.BoardRepository;
import xyz.moodf.board.controllers.RequestPostBoard;
import xyz.moodf.global.validators.PasswordValidator;
import xyz.moodf.member.libs.MemberUtil;


@Lazy
@Component
@RequiredArgsConstructor
public class BoardDataValidator implements Validator, PasswordValidator {
    private final BoardRepository repository;
    private final MemberUtil memberUtil;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestPostBoard.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }
        RequestPostBoard form = (RequestPostBoard) target;
        String mode = form.getMode();
        String bid = form.getBid();

        // 글 수정이면 seq 필수
        if (mode.equals("update") && (form.getSeq() == null || form.getSeq() < 1L)) {
//            errors.rejectValue("seq", "NotNull.board.seq");
        }

        // 제목 필수
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "NotBlank.board.subject");

        // 내용 필수
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "NotBlank.board.content");

        // 비회원인 경우 작성자명 필수
        if (form.isGuest()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "poster", "NotBlank.board.poster");

            String guestPw=form.getGuestPw();
            // 비회원인 경우 비밀번호 필수
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "guestPw", "NotBlank.board.guestPw");

            if (!checkAlpha(guestPw, true) || !checkNumber(guestPw) || guestPw.length() < 4){ // 비밀번호 복잡성도 체크
                errors.rejectValue("Complexity", "Format.board.guestPw");
            }
        }
    }
}
