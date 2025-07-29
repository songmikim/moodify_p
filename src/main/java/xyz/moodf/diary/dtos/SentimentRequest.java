package xyz.moodf.diary.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SentimentRequest {
    @NotBlank(message = "{NotBlank.sentimentRequest.content}")
    private String content;

    @NotBlank(message = "{NotBlank.sentimentRequest.sentiments}")
    private String sentiments;
}
