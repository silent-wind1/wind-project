package com.yefeng.yefengaicode.exception;

import lombok.Getter;

@Getter
public enum FileCode {

    FILE_IS_BLANK(50100, "文件不能为空");


    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    FileCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
