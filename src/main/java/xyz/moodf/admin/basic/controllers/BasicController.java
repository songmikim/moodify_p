package xyz.moodf.admin.basic.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.moodf.admin.global.controllers.CommonController;
import xyz.moodf.global.annotations.ApplyCommonController;
import xyz.moodf.global.codevalue.services.CodeValueService;
import xyz.moodf.global.exceptions.script.AlertException;
import xyz.moodf.global.file.controllers.RequestUpload;
import xyz.moodf.global.file.services.FileUploadService;

import java.util.UUID;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/admin/config")
public class BasicController extends CommonController {

    private final FileUploadService fileUploadService;
    private final CodeValueService codeValueService;

    @Override
    @ModelAttribute("mainCode")
    public String mainCode() {
        return "basic";
    }

    @GetMapping
    public String basicConfig(Model model) {
        commonProcess("basic", model);

        return "admin/basic/index";
    }

    @GetMapping("/image")
    public String imageConfig(Model model) {
        commonProcess("image", model);

        return "admin/basic/image";
    }

    @PostMapping("/image")
    public String imageProcess(@RequestParam(name = "code", required = false) String code,
                               @RequestPart(name = "file", required = false) MultipartFile file,
                               Model model) {

        if (!StringUtils.hasText(code)) {
            throw new AlertException("이미지 코드를 입력하세요.");
        }

        if (file == null) {
            throw new AlertException("이미지를 업로드 하세요.");
        }

        String gid = UUID.randomUUID().toString();
        RequestUpload form = new RequestUpload();
        form.setGid(gid);
        form.setImageOnly(true);
        form.setFile(new MultipartFile[]{file});
        form.setSingle(true);

        fileUploadService.uploadProcess(form);
        fileUploadService.processDone(gid);

        codeValueService.set(code, gid, false);

        model.addAttribute("script", "parent.location.reload();");
        return "common/_execute_script";
    }

    private void commonProcess(String code, Model model) {

        model.addAttribute("subCode", code);
    }
}
