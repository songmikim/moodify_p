package xyz.moodf.diary.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import xyz.moodf.diary.constants.Weather;

import java.time.LocalDate;

@Data
public class DiaryRequest {
    @NotBlank(message = "{NotBlank.diaryRequest.title}")
    private String title;

    @NotBlank(message = "{NotBlank.diaryRequest.content}")
    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{NotBlank.diaryRequest.date}")  // 크롬 개발자 도구 등으로 값을 지울 경우를 막기 위함
    private LocalDate date;

    @NotNull(message = "{NotBlank.diaryRequest.weather}")  // 크롬 개발자 도구 등으로 값을 지울 경우를 막기 위함
    private Weather weather = Weather.NULL;

    private String gid;
}
