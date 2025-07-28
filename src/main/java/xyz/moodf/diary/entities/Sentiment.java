package xyz.moodf.diary.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Sentiment {
    @Id
    @Column(length = 45)
    private String gid;

    @Column(length=2000)
    private String content;

    @Column(length=2000)
    private String sentiments;
}
