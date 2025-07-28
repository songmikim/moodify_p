package xyz.moodf.diary.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.moodf.diary.dtos.SentimentRequest;
import xyz.moodf.diary.services.SentimentService;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.libs.MemberUtil;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sentiment")
public class SentimentController {

    private final MemberUtil memberUtil;
    private final SentimentService sentimentService;

    @PostMapping("/update-content")
    public ResponseEntity<Void> updateContent(@RequestParam SentimentRequest request, @RequestBody Map<String, String> body) {
        String content = body.get("content");
        Member member = memberUtil.getMember();


        sentimentService.update(request, member, request.getDate());
        return ResponseEntity.ok().build();
    }
}
