package xyz.moodf.diary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.entities.DiaryId;

public interface DiaryRepository extends JpaRepository<Diary, DiaryId>, QuerydslPredicateExecutor<Diary> {

}
