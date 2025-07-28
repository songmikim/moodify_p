package xyz.moodf.global.file.controllers;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RequestUpload {
    private String gid;
    private String location;
    private MultipartFile[] file;
    private boolean single;
    private boolean imageOnly;
}
