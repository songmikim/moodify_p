package xyz.moodf.board.comment.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.moodf.board.comment.entities.Comment;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class CommentPermissionService {

    private final MemberUtil memberUtil;

    public boolean canDeleteC(Comment comment) {
        // 관리자는 모든 글 삭제 가능
        if (memberUtil.isAdmin()) {
            return true;
        }

        // 회원 글인 경우 본인 확인
        if (comment.getMember() != null) {
            Member currentMember = memberUtil.getMember();
            if (currentMember == null) {
                return false; // NPE 방지
            }

            return Objects.equals(
                    comment.getMember().getEmail(),
                    currentMember.getEmail()
            );
        } else {
            // 비회원 글은 비밀번호 확인이 필요
            return true;
        }
    }


    public boolean needAuth(Comment comment) {
        // 관리자는 멤버와 상관없음
        if (memberUtil.isAdmin()) {
            return false;
        }

        // 회원인지 아닌지 판가름
        if (comment.getMember() != null) {
            return false;
        } else {
            return true;
        }
    }
}
