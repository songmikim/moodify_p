package xyz.moodf.global.file;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import xyz.moodf.global.file.controllers.RequestThumb;
import xyz.moodf.global.file.controllers.RequestUpload;
import xyz.moodf.global.file.entities.FileInfo;
import xyz.moodf.global.file.repositories.FileInfoRepository;
import xyz.moodf.global.file.services.FileUploadService;
import xyz.moodf.global.file.services.ThumbnailService;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class ThumbnailServiceTest {
    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private FileUploadService uploadService;


    @Test
    void test() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "image".getBytes()
        );
        RequestUpload upload = new RequestUpload();

        upload.setFile(new MockMultipartFile[]{file});
        upload.setGid(UUID.randomUUID().toString());
        upload.setLocation("C:/uploads");
        upload.setSingle(false);
        upload.setImageOnly(true);

        List<FileInfo> result = uploadService.uploadProcess(upload);
        FileInfo uploaded = result.get(0);

        RequestThumb request = new RequestThumb(); // 썸네일 생성 요청 객체 생성
        request.setSeq(uploaded.getSeq()); // 썸네일 생성 원본 파일의 시퀀스 번호 세팅
        request.setWidth(100);
        request.setHeight(100);
        request.setCrop(true);

        // 썸네일 서비스 호출, 썸네일 생성 후 URL 반환
        String thumbnailUrl = thumbnailService.create(request);
        System.out.println("생성된 썸네일 URL: " + thumbnailUrl);
    }
}
