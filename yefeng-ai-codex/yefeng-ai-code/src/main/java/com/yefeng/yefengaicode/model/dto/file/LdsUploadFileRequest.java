package com.yefeng.yefengaicode.model.dto.file;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class LdsUploadFileRequest implements Serializable {
    /**
     * 文件所属对象id
     */
    @NotBlank(message = "文件所属对象id不能为空")
    private String relateId;
}