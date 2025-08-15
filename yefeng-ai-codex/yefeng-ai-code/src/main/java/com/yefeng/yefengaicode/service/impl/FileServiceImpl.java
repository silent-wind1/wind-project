package com.yefeng.yefengaicode.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.yefeng.yefengaicode.common.BaseResponse;
import com.yefeng.yefengaicode.common.ResultUtils;
import com.yefeng.yefengaicode.exception.BusinessException;
import com.yefeng.yefengaicode.exception.ErrorCode;
import com.yefeng.yefengaicode.exception.FileException;
import com.yefeng.yefengaicode.exception.FileUploadBizEnum;
import com.yefeng.yefengaicode.model.dto.file.LdsUploadFileRequest;
import com.yefeng.yefengaicode.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;

import static com.yefeng.yefengaicode.exception.FileCode.FILE_IS_BLANK;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    private final String profile = "D:/img/";
    private final String prefix = "yefeng-";

    /**
     * 文件上传
     *
     * @param file        文件
     * @param fileRequest 文件请求
     * @return 文件路径
     */
    @Override
    public BaseResponse<String> uploadFile(MultipartFile file, LdsUploadFileRequest fileRequest) {
        if (file.isEmpty()) {
            throw new FileException(FILE_IS_BLANK);
        }
        String filename = prefix + IdUtil.simpleUUID();
        String filepath = profile + filename + "." + FileUtil.getSuffix(file.getOriginalFilename());
        try {
            file.transferTo(new File(filepath));
        } catch (Exception e) {
            throw new FileException(e);
        }
        return ResultUtils.success(filepath);
    }

    @Override
    public byte[] downloadFile(String fileId) {
        return new byte[0];
    }


    /**
     * 校验文件
     *
     * @param multipartFile     文件
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
