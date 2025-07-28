package xyz.moodf.board.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.moodf.board.entities.BoardData;
import xyz.moodf.global.exceptions.UnAuthorizedException;
import xyz.moodf.member.libs.MemberUtil;

@Service
@RequiredArgsConstructor
public class BoardPermissionService {
    private final MemberUtil memberUtil;

    /**
     * 수정 권한 확인
     */
    public boolean canEdit(BoardData boardData) {
        // 관리자는 모든 글 수정 가능
        if (memberUtil.isAdmin()) {
            return true;
        }

        // 회원 글인 경우 본인 확인
        if (boardData.getMember() != null) {
            if (!memberUtil.isLogin() ||
                    !boardData.getMember().getEmail().equals(memberUtil.getMember().getEmail())) {
                return false;
            } else {
                return true;
            }
        } else {
            // 비회원 글은 비밀번호 확인이 필요
            return true;
        }
    }

    /**
     * 삭제 권한 확인
     */
    public boolean canDelete(BoardData boardData) {
        // 관리자는 모든 글 삭제 가능
        if (memberUtil.isAdmin()) {
            return true;
        }

        // 회원 글인 경우 본인 확인
        if (boardData.getMember() != null) {
            if (!memberUtil.isLogin() ||
                    !boardData.getMember().getEmail().equals(memberUtil.getMember().getEmail())) {
                return false;
            } else {
                return true;
            }
        } else {
            // 비회원 글은 비밀번호 확인이 필요
            return true;
        }
    }

    /**
     * 조회 권한 확인 (비밀글인 경우)
     */
    public boolean canView(BoardData boardData) {
        // 비밀글이 아니면 누구나 조회 가능
        if (!boardData.isSecret()) {
            return true;
        }

        // 관리자는 모든 글 조회 가능
        if (memberUtil.isAdmin()) {
            return true;
        }

        // 작성자 본인만 조회 가능
        if (boardData.getMember() != null) {
            if (!memberUtil.isLogin() ||
                    !boardData.getMember().getEmail().equals(memberUtil.getMember().getEmail())) {
                return false;
            } else {return true;}
        }

        return false;
    }
}