package xyz.moodf.diary.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import xyz.moodf.diary.constants.Weather;
import xyz.moodf.global.entities.BaseEntity;
import xyz.moodf.member.entities.Member;

import java.time.LocalDate;

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

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)  // DB에서 직접 null로 세팅할 경우를 막기 위해 추가
    private Weather weather = Weather.NULL;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Member member;
}
