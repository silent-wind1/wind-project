package com.yefeng.yefengaicode.exception;

import lombok.Getter;

/**
 * 文件异常
 */
@Getter
public class FileException extends RuntimeException {
    /**
     * 错误码
     */
    private final int code;

    public FileException(String message, int code) {
        super(message);
        this.code = code;
    }

    public FileException(FileCode fileCode){
        super(fileCode.getMessage());
        this.code = fileCode.getCode();
    }

    public FileException(FileCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public FileException(Exception e) {
        super(e);
        this.code = 50100;
    }
}
