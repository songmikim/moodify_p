package xyz.moodf.diary.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Entity
public class Sentiment implements Serializable {
    @Id
    @GeneratedValue
    private Long sid;

    @Column(length=2000)
    private String content;

    @Column(length=2000)
    private String sentiment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "did")
    @ToString.Exclude
    private Diary diary;
}
