package com.yefeng.yefengaicode.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.yefeng.yefengaicode.common.BaseResponse;
import com.yefeng.yefengaicode.common.ResultUtils;
import com.yefeng.yefengaicode.config.FileUploadConfig;
import com.yefeng.yefengaicode.exception.BusinessException;
import com.yefeng.yefengaicode.exception.ErrorCode;
import com.yefeng.yefengaicode.exception.FileException;
import com.yefeng.yefengaicode.exception.FileUploadBizEnum;
import com.yefeng.yefengaicode.listener.ExcelListener;
import com.yefeng.yefengaicode.model.dto.file.LdsUploadFileRequest;
import com.yefeng.yefengaicode.model.entity.App;
import com.yefeng.yefengaicode.service.AppService;
import com.yefeng.yefengaicode.service.FileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.yefeng.yefengaicode.exception.FileCode.FILE_IS_BLANK;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Resource
    private FileUploadConfig fileUploadConfig;

    @Resource
    private AppService appService;

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
        String filename = fileUploadConfig.getPrefix() + IdUtil.simpleUUID();
        String filepath = fileUploadConfig.getImagePath() + File.separator + filename + "." + FileUtil.getSuffix(file.getOriginalFilename());
        try {
            File filePath = new File(fileUploadConfig.getBasePath() + File.separator + filepath);
            if (!filePath.getParentFile().exists()) {
                boolean mkdir = filePath.getParentFile().mkdirs();
                if (!mkdir) {
                    throw new FileException("文件上传失败", 500);
                }
            }
            file.transferTo(filePath);
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
     * 导入Excel文件内容数据 - 支持百万级数据多线程导入
     *
     * @param file    excel文件
     * @param request
     * @return
     */
    @Override
    public BaseResponse<String> importExcel(MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new FileException(FILE_IS_BLANK);
        }
        // 获取excel扩展名
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        ExcelTypeEnum excelType = switch (suffix) {
            case "xls" -> ExcelTypeEnum.XLS;
            case "xlsx" -> ExcelTypeEnum.XLSX;
            case null, default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
        };
        // 获取excel sheet数量
        int count = getSheetCount(file);
        // 创建一个固定大小的线程池，大小与sheet数量相同
        ExecutorService executor = Executors.newFixedThreadPool(count);
        for (int sheetNo = 0; sheetNo < count; sheetNo++) {
            // 向线程池提交一个任务
            int finalSheetNo = sheetNo;
            try {
                InputStream inputStream = file.getInputStream();
                executor.submit(() -> {
                    // 使用EasyExcel读取指定的sheet
                    EasyExcel.read(inputStream, App.class, new ExcelListener(appService)).excelType(excelType).sheet(finalSheetNo).doRead();
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 启动线程池的关闭序列
        executor.shutdown();
        // 等待所有任务完成，或者在等待超时前被中断
        try {
            boolean result = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            if (!result) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // 如果等待过程中线程被中断，打印异常信息
            log.error("线程池等待任务完成时被中断", e);
        }
        return ResultUtils.success(null);
    }

    @Override
    public BaseResponse<String> exportExcel() {
        return null;
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

    /**
     * 获取excel sheet数量
     *
     * @param file excel文件
     * @return sheet数量
     */
    private int getSheetCount(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            ExcelReader reader = EasyExcel.read(inputStream).build();
            return reader.excelExecutor().sheetList().size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
