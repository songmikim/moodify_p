package xyz.moodf.global.file.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.moodf.global.file.entities.FileInfo;
import xyz.moodf.global.file.services.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/file", "/file"})
@Tag(name="파일 API", description = "파일 업로드, 다운로드, 조회기능을 제공")
public class FileController {
    private final FileUploadService uploadService;
    private final FileDeleteService deleteService;
    private final FileInfoService infoService;
    private final FileDownloadService downloadService;
    private final ThumbnailService thumbnailService;

    @Operation(summary = "파일 업로드 처리", method = "MULTIPART")
    @ApiResponse(responseCode = "201", description = "파일 업로드 성공시 업로드한 파일 목록이 출력")
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public List<FileInfo> upload(@ModelAttribute RequestUpload form) {
        String gid = form.getGid();
        if (gid == null || gid.trim().isEmpty()) {
            gid = UUID.randomUUID().toString(); // 자동 생성
            form.setGid(gid);
        }
        List<FileInfo> items = uploadService.uploadProcess(form);
        return items;
    }

    @GetMapping({"/list/{gid}", "/list/{gid}/{location}"})
    public List<FileInfo> list(@PathVariable("gid") String gid, @PathVariable(name="location", required = false) String location) {

        List<FileInfo> items = infoService.getList(gid, location);

        return items;
    }

    @Operation(summary = "파일 등록번호로 파일 정보 한개 조회")
    @Parameters({
            @Parameter(name="seq", in= ParameterIn.PATH, required = true, description = "파일 등록 번호")
    })
    @GetMapping("/info/{seq}")
    public FileInfo info(Long seq) {
        FileInfo item = infoService.get(seq);

        return item;
    }

    @DeleteMapping("/delete/{seq}")
    public FileInfo delete(@PathVariable("seq") Long seq) {
        FileInfo item = deleteService.deleteProcess(seq);

        return item;
    }

    @DeleteMapping({"/deletes/{gid}", "/deletes/{gid}/{location}"})
    public List<FileInfo> deletes(@PathVariable("gid") String gid, @PathVariable(name="location", required = false) String location) {
        List<FileInfo> items = deleteService.deleteProcess(gid, location);

        return items;
    }
    /**
     * 파일 다운로드
     */
    @GetMapping("/download/{seq}")
    public void download(@PathVariable("seq") Long seq) {
        downloadService.process(seq);
    }

    @GetMapping("/thumb")
    public void thumb(RequestThumb form, HttpServletResponse response) {
        String path = thumbnailService.create(form);
        if (!StringUtils.hasText(path)) {
            return;
        }

        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            String contentType = Files.probeContentType(file.toPath()); // 이미지 파일 형식
            response.setContentType(contentType);

            OutputStream out = response.getOutputStream();
            out.write(bis.readAllBytes());
            System.out.println("썸네일 생성 경로: " + path);
            System.out.println("파일 존재 여부: " + new File(path).exists());
        } catch (IOException e) {}
    }

    @GetMapping("/image/{seq}")
    public ResponseEntity<byte[]> showImage(@PathVariable("seq") Long seq) {
        FileInfo item = infoService.get(seq);

        String contentType = item.getContentType();
        byte[] bytes = null;
        File file = new File(item.getFilePath());
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            bytes = bis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(contentType));

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
