package xyz.moodf.board.comment.entities;

import jakarta.persistence.*;
import lombok.Data;
import xyz.moodf.global.entities.BaseEntity;
import xyz.moodf.member.entities.Member;

@Data
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comment_board", columnList = "boardDataSeq,deleted,createdAt")
})
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue
    private Long seq;           // 댓글 ID

    @Column(nullable = false)
    private Long boardDataSeq;      // 게시글 번호 (어떤 글의 댓글인지)

    @Column(length = 60, nullable = false)
    private String commenter;      // 작성자명

    @Lob
    @Column(nullable = false)
    private String content;     // 댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;          // 회원이 작성한 경우

    // 비회원 정보
    @Column(length = 65)
    private String guestPw;         // 비회원 비밀번호

    @Column(length = 20)
    private String ip;              // 작성자 IP

    @Column(length = 255)
    private String ua;              // User-Agent

    private boolean deleted;
}
