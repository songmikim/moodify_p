package xyz.moodf.board.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import xyz.moodf.member.constants.Authority;

@Data
public class RequestBoard {
    private String mode;

    @NotBlank
    private String bid; // 게시판 아이디

    @NotBlank
    private String name; // 게시판 이름

    private int rowsForPage; // 한페이지당 레코드 갯수

    private int pageCount; // 노출될 페이지 갯수

    private String skin; // 게시판 스킨

    private String category; // 게시판 분류

    private boolean active; // 게시판 사용 여부, true - 사용, false : 미사용
    private boolean editor;  // 에디터 사용 여부
    private boolean imageUpload; // 에디터에 이미지 추가 기능 사용 여부
    private boolean attachFile; // 파일 첨부 기능 사용 여부
    private boolean comment; // 댓글 사용 여부

    private Authority listAuthority; // 목록 권한, ALL - 전체, MEMBER - 회원, ADMIN - 관리자

    private Authority viewAuthority; // 글보기 권한

    private Authority writeAuthority; // 글작성 권한

    private Authority commentAuthority; // 댓글 작성 권한
}