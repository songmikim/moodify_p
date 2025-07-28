package xyz.moodf.board.validator;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import xyz.moodf.admin.board.entities.Board;
import xyz.moodf.admin.board.repositories.BoardRepository;
import xyz.moodf.admin.board.exceptions.BoardNotFoundException;
import xyz.moodf.board.controllers.RequestPostBoard;
import xyz.moodf.global.validators.PasswordValidator;
import xyz.moodf.member.constants.Authority;
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
            errors.rejectValue("seq", "NotNull");
        }

        // 제목 필수
        if (!StringUtils.hasText(form.getSubject())) {
            errors.rejectValue("subject", "NotBlank");
        }

        // 내용 필수
        if (!StringUtils.hasText(form.getContent())) {
            errors.rejectValue("content", "NotBlank");
        }

        // 비회원인 경우 작성자명 필수
        if (form.isGuest()) {
            if (!StringUtils.hasText(form.getPoster())) {
            errors.rejectValue("poster", "NotBlank");
            }

            String guestPw=form.getGuestPw();
        // 비회원인 경우 비밀번호 필수
            if (!StringUtils.hasText(guestPw)) {
                errors.rejectValue("guestPw", "NotBlank");
            } else if (!checkAlpha(guestPw, true) || !checkNumber(guestPw) || guestPw.length() < 4){ // 비밀번호 복잡성도 체크
                errors.rejectValue("Complexity", "guestPw");
            }
        }
    }
}
