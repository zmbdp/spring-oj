package com.zmbdp.friend.controller.file;

import com.zmbdp.common.core.domain.Result;
import com.zmbdp.common.file.domain.OSSResult;
import com.zmbdp.friend.service.file.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private IFileService sysFileService;

    @PostMapping("/upload")
    public Result<OSSResult> upload(@RequestBody MultipartFile file) {
        return Result.success(sysFileService.upload(file));
    }
}
