package xyz.moodf.admin.diary.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentImageDto {
    private String sentiment;
    private String imagePath;
}