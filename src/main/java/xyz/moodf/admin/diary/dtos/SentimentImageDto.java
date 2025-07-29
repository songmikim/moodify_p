package xyz.moodf.admin.diary.dtos;

import lombok.Data;

@Data
public class SentimentImageDto {
    private String sentiment;
    private String imagePath;
}