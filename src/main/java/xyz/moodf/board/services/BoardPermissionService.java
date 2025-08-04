package xyz.moodf.board.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.moodf.admin.board.entities.Board;
import xyz.moodf.board.entities.BoardData;
import xyz.moodf.board.entities.GuestAuth;
import xyz.moodf.member.constants.Authority;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardPermissionService {
    private final MemberUtil memberUtil;

    public boolean authCheck(Authority auth) {
        return switch (auth) {
            case ALL ->
                // 모든 사용자 (회원, 비회원, 관리자) 권한 있음
                    true;
            case USER ->
                // 회원(USER) 또는 관리자(ADMIN) 권한 있음
                    memberUtil.isLogin();
            case ADMIN ->
                // 관리자(ADMIN)만 권한 있음
                    memberUtil.isAdmin();
            default -> false;
        };
    }

    public boolean canPost(Board board){
        Authority auth=board.getWriteAuthority();
        System.out.println(auth);

        return authCheck(auth);
    }

    /**
     * 수정 권한 확인
     */
    public boolean canEdit(BoardData boardData) {
        Authority auth=boardData.getBoard().getWriteAuthority();

        if (authCheck(auth)) {

            // 회원 글인 경우 본인 확인
            if (boardData.getMember() != null) {
                Member currentMember = memberUtil.getMember();
                if (currentMember == null) {
                    return false; // NPE 방지
                }

                return Objects.equals(
                        boardData.getMember().getEmail(),
                        currentMember.getEmail()
                );
            } else {
                // 비회원 글은 비밀번호 확인이 필요
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 삭제 권한 확인
     */
    public boolean canDelete(BoardData boardData) {
        Authority auth = boardData.getBoard().getWriteAuthority();

        if (authCheck(auth)) {

            // 관리자는 모든 글 삭제 가능
            if (memberUtil.isAdmin()) {
                return true;
            }

            // 회원 글인 경우 본인 확인
            if (boardData.getMember() != null) {
                Member currentMember = memberUtil.getMember();
                if (currentMember == null) {
                    return false; // NPE 방지
                }

                return Objects.equals(
                        boardData.getMember().getEmail(),
                        currentMember.getEmail()
                );
            } else {
                // 비회원 글은 비밀번호 확인이 필요
                return true;
            }
        } else {
            return false;
        }
    }
    /**
     * 조회 권한 확인 (비밀글인 경우)
     */
    public boolean canView(BoardData boardData) {
        Authority auth = boardData.getBoard().getViewAuthority();

        if (authCheck(auth)) {

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
                } else {
                    return true;
                }
            }
            return false;
        } else {return false;}
    }

    public boolean isGuest(BoardData boardData) {
        // 관리자는 멤버와 상관없음
        if (memberUtil.isAdmin()) {
            return false;
        }

        // 회원인지 아닌지 판가름
        if (boardData.getMember() != null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean guestPwCheck(GuestAuth data, String guestPw) {
        // null 체크 추가
        if (data.getGuestPw() == null || guestPw == null) {
            return false;
        }
        // trim()으로 공백 제거 후 비교
        return guestPw.trim().equals(data.getGuestPw().trim());
    }

}
