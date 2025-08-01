package xyz.moodf.admin.diary.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.moodf.admin.diary.dtos.SentimentImageDto;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.configs.FileProperties;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.libs.MemberUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/admin/diary")
public class ImageChangeController {

    private final Utils utils;
    private final MemberUtil memberUtil;
    private final FileProperties fileProperties;

    @GetMapping("/image")
    public String changeImages(Model model) {
        commonProcess(model);

        List<SentimentImageDto> sentimentImages = new ArrayList<>();
        String imagePathRoot = "/common/images/sentiments/";

        // 6개의 대분류 감정 하드코딩
        sentimentImages.add(new SentimentImageDto("happiness", imagePathRoot + "happiness.png"));
        //sentimentImages.add(new SentimentImageDto("sadness", imagePathRoot + "sadness.png"));

        model.addAttribute("sentimentImages", sentimentImages);
        return "admin/diary/image";
    }

    @PostMapping("/image/upload")
    @ResponseBody
    public ResponseEntity<String> uploadSentimentImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sentiment") String sentiment) throws IOException {

        String fileName = sentiment + ".png";
        Path savePath = Paths.get(fileProperties.getPath(), "sentiments");

        // 디렉토리 없으면 생성
        if (!Files.exists(savePath)) {
            Files.createDirectories(savePath);
        }

        Path filePath = savePath.resolve(fileName);
        file.transferTo(filePath.toFile());

        // 클라이언트가 접근 가능한 URL 반환
        String publicUrl = fileProperties.getUrl() + "/sentiments/" + fileName;
        return ResponseEntity.ok(publicUrl);
    }

    private void commonProcess(Model model) {

        String pageTitle = "일기 감정 이미지 관리";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();

        addCommonScript.add("fileManager");
        addCommonScript.add("sentiment_image");
        pageTitle = utils.getMessage("일기 감정 이미지 관리");

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("pageTitle", pageTitle);
    }
}
