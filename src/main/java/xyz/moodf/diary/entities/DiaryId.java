package xyz.moodf.diary.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.moodf.member.entities.Member;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DiaryId implements Serializable {
    private Member member;
    private LocalDate date;
}
