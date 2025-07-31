package xyz.moodf.board.comment.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestComment {
    private String mode;            // register, update, delete
    private Long seq;               // 수정/삭제시 댓글 ID
    private Long boardDataSeq;          // 게시글 ID
    private Long parentSeq;         // 대댓글인 경우 부모 댓글 ID

    @NotBlank
    private String commenter;          // 작성자명

    private String guestPw;         // 비회원 비밀번호

    @NotBlank
    private String content;         // 댓글 내용

    private boolean secret;         // 비밀 댓글 여부
    private boolean guest;          // 비회원인지 여부

}