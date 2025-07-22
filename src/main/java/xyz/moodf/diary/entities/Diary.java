package xyz.moodf.diary.entities;

import jakarta.persistence.*;
import lombok.Data;
import xyz.moodf.global.entities.BaseEntity;
import xyz.moodf.member.entities.Member;

@Data
@Entity
public class Diary extends BaseEntity {
    @Id
    @GeneratedValue
    private Long did;

    @Column(length=50, nullable = false)
    private String title;

    @Column(length=2000, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
