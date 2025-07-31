package xyz.moodf.diary.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import xyz.moodf.diary.constants.Weather;
import xyz.moodf.global.entities.BaseEntity;
import xyz.moodf.member.entities.Member;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@IdClass(DiaryId.class)
public class Diary extends BaseEntity implements Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Member member;

    @Id
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date;

    @Column(length=50, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)  // DB에서 직접 null로 세팅할 경우를 막기 위해 추가
    private Weather weather = Weather.NULL;

    @Column(length = 45, nullable = false, unique = true)
    private String gid;

    private String sentiments;
}
