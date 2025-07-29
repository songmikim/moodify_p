package xyz.moodf.admin.diary.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.moodf.admin.diary.dtos.SentimentImageDto;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.libs.Utils;
import xyz.moodf.member.libs.MemberUtil;

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

    @GetMapping("/image")
    public String changeImages(Model model) {
        commonProcess(model);

        List<SentimentImageDto> sentimentImages = new ArrayList<>();

        // 감정 이름, 이미지 경로 정의 필요

        model.addAttribute("sentimentImages", sentimentImages);
        return "admin/diary/image";
    }

    @PostMapping("/image/upload")
    @ResponseBody
    public ResponseEntity<String> uploadSentimentImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sentiment") String sentimentName
    ) {
        try {
            String filename = sentimentName + ".png";
            String uploadDir = "/common/images/sentiments/";

            // 파일 저장 (경로는 프로젝트 상황에 따라 조정 필요)
            Path path = Paths.get("src/main/resources/static" + uploadDir + filename);
            Files.createDirectories(path.getParent());
            file.transferTo(path.toFile());

            return ResponseEntity.ok(uploadDir + filename);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    private void commonProcess(Model model) {

        String pageTitle = "일기 감정 이미지 관리";
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addCommonCss = new ArrayList<>();

        addCommonScript.add("fileManager");
        addScript.add("admin/js/diary/sentiment_image");
        pageTitle = utils.getMessage("일기 감정 이미지 관리");

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("pageTitle", pageTitle);
    }
}
