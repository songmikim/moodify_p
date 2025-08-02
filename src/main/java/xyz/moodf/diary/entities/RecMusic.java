package xyz.moodf.diary.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class RecMusic implements Serializable {
    @Id
    @Column(length = 45)
    private String gid;

    @Column(length=2000)
    private String musics;

}
