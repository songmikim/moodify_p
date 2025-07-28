package xyz.moodf.diary.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SentimentRequest {
    @NotBlank(message = "{NotBlank.sentimentRequest.content}")
    private String content;

    @NotBlank(message = "{NotBlank.sentimentRequest.sentiments}")
    private String sentiments;

    @NotNull(message = "{NotBlank.sentimentRequest.date}")
    private LocalDate date;
}
