package xyz.moodf.diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.moodf.diary.entities.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

}
