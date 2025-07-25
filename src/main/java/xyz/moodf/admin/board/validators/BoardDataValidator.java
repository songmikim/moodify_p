package xyz.moodf.board.validators;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import xyz.moodf.board.controllers.RequestPostBoard;
import xyz.moodf.board.entities.Board;
import xyz.moodf.board.exceptions.BoardNotFoundException;
import xyz.moodf.board.repositories.BoardDataRepository;
import xyz.moodf.board.repositories.BoardRepository;
import xyz.moodf.member.constants.Authority;
import xyz.moodf.member.libs.MemberUtil;


@Lazy
@Component
@RequiredArgsConstructor
public class BoardDataValidator implements Validator {
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

        // 제목 필수
        if (!StringUtils.hasText(form.getSubject())) {
            errors.rejectValue("subject", "NotBlank");
        }

        // 내용 필수
        if (!StringUtils.hasText(form.getContent())) {
            errors.rejectValue("content", "NotBlank");
        }

        // 비회원인 경우 작성자명 필수
        if (form.isGuest() && !StringUtils.hasText(form.getPoster())) {
            errors.rejectValue("poster", "NotBlank");
        }

        // 비회원인 경우 비밀번호 필수
        if (form.isGuest() && !StringUtils.hasText(form.getGuestPw())) {
            errors.rejectValue("guestPw", "NotBlank");
        }

        Board board = repository.findById(form.getBid()).orElseThrow(BoardNotFoundException::new);
        if (board.getWriteAuthority() == Authority.USER && !memberUtil.isLogin()) {
            errors.reject("NoPermission.write");
        }
    }
}
