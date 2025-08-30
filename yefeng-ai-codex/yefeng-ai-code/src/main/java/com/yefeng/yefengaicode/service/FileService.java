package com.yefeng.yefengaicode.service;

import com.yefeng.yefengaicode.common.BaseResponse;
import com.yefeng.yefengaicode.model.dto.file.LdsUploadFileRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件路径
     */
    BaseResponse<String> uploadFile(MultipartFile file, LdsUploadFileRequest fileRequest);

    /**
     * 下载文件
     *
     * @param fileId 文件id
     * @return 文件
     */
    byte[] downloadFile(String fileId);

    BaseResponse<String> importExcel(MultipartFile file, HttpServletRequest request);
}
