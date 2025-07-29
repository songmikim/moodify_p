package xyz.moodf.diary.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import xyz.moodf.diary.constants.Weather;

import java.time.LocalDate;

@Getter @Setter
public class DiaryRequest {
    @NotBlank(message = "{NotBlank.diaryRequest.title}")
    private String title;

    @NotBlank(message = "{NotBlank.diaryRequest.content}")
    private String content;

    @NotNull(message = "{NotBlank.diaryRequest.date}")  // 크롬 개발자 도구 등으로 값을 지울 경우를 막기 위함
    private LocalDate date;

    @NotNull(message = "{NotBlank.diaryRequest.weather}")  // 크롬 개발자 도구 등으로 값을 지울 경우를 막기 위함
    private Weather weather = Weather.NULL;
}
