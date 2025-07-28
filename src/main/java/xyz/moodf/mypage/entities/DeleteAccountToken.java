package xyz.moodf.mypage.entities;

import jakarta.persistence.*;
import lombok.Data;
import xyz.moodf.global.entities.BaseEntity;
import xyz.moodf.member.entities.Member;

import java.time.LocalDateTime;

@Data
@Entity
public class DeleteAccountToken extends BaseEntity {
    @Id
    @GeneratedValue
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_seq")
    private Member member;

    @Column(length = 80, nullable = false, unique = true)
    private String token;

    private LocalDateTime expireAt;
}
