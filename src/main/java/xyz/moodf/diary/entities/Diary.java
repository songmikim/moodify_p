package xyz.moodf.diary.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import xyz.moodf.diary.constants.Weather;
import xyz.moodf.global.entities.BaseEntity;
import xyz.moodf.global.file.entities.FileInfo;
import xyz.moodf.member.entities.Member;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false, length = 10)  // DB에서 직접 null로 세팅할 경우를 막기 위해 추가
    @Enumerated(EnumType.STRING)
    private Weather weather = Weather.NULL;

    @Column(length = 45, nullable = false, unique = true)
    private String gid;

    private String sentiments;

    @Transient
    private Map<String, Integer> statistics;

    @Transient
    private String strongest;

    @Transient
    private List<String> ranking;

    @Transient
    private FileInfo icon;
}
