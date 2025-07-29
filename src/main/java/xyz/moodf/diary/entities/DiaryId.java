package xyz.moodf.diary.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DiaryId implements Serializable {
    private Long member;
    private LocalDate date;
}
