package xyz.moodf.diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.moodf.diary.entities.Diary;
import xyz.moodf.diary.repositories.DiaryRepository;
import xyz.moodf.member.entities.Member;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public Diary saveDiary(String title, String content, Member member) {
        Diary diary = new Diary();
        diary.setTitle(title);
        diary.setContent(content);
        diary.setMember(member);

        return diaryRepository.save(diary);
    }
}