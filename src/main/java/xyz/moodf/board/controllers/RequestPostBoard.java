package xyz.moodf.board.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestPostBoard {
    private String mode;
    private Long seq;

    @NotBlank
    private String bid;

    @NotBlank
    private String gid;

    @NotBlank
    private String poster;
    private String guestPw;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;
    private boolean notice; // 공지글 여부
    private boolean secret; // 비밀글 여부

    private boolean guest;
}
