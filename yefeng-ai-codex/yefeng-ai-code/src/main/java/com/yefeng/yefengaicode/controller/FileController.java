package com.yefeng.yefengaicode.controller;

import com.yefeng.yefengaicode.common.BaseResponse;
import com.yefeng.yefengaicode.model.dto.file.LdsUploadFileRequest;
import com.yefeng.yefengaicode.service.FileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//@Valid
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private FileService fileService;

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件地址
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile file, LdsUploadFileRequest fileRequest, HttpServletRequest request) {
        return fileService.uploadFile(file, fileRequest);
    }
}
