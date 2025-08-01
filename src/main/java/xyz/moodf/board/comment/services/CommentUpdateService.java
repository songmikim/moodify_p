package xyz.moodf.board.comment.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.moodf.board.comment.controllers.RequestComment;
import xyz.moodf.board.comment.entities.Comment;
import xyz.moodf.board.comment.repositories.CommentRepository;
import xyz.moodf.member.libs.MemberUtil;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentUpdateService {
    private final CommentRepository repository;
    private final HttpServletRequest request;
    private final MemberUtil memberUtil;

    public void process(RequestComment form) {
        String mode = Objects.requireNonNullElse(form.getMode(), "register");

        Comment comment = new Comment();
        comment.setBoardDataSeq(form.getBoardDataSeq());
        comment.setCommenter(form.getCommenter());
        comment.setContent(form.getContent());
        comment.setIp(request.getRemoteAddr());
        comment.setDeleted(false);
        comment.setUa(request.getHeader("User-Agent"));

        if (memberUtil.isLogin()) {
            comment.setMember(memberUtil.getMember());
            comment.setCommenter(memberUtil.getMember().getName());
        } else {
            comment.setGuestPw(form.getGuestPw());
            comment.setCommenter(form.getCommenter());
        }

        repository.save(comment);
    }
}
