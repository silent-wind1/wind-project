package com.yefeng.yefengaicode.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yefeng.yefengaicode.ai.model.HtmlCodeResult;
import com.yefeng.yefengaicode.ai.model.MultiFileCodeResult;
import com.yefeng.yefengaicode.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 代码文件保存器
 */
public class CodeFileSaver {
    // 代码文件保存根路径
    private final static String FILE_SAVE_ROOT_PATH = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 保存HTML代码文件
     * @param htmlCodeResult html代码结果
     * @return 文件目录
     */
    public static File saveCodeHtmlCodeFile(HtmlCodeResult htmlCodeResult) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath, "index.html", htmlCodeResult.getHtmlCode());
        return new File(baseDirPath);
    }

    /**
     * 保存 MultiFileCodeResult
     * @param result 多文件代码结果
     * @return 文件目录
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult result) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        writeToFile(baseDirPath, "style.css", result.getCssCode());
        writeToFile(baseDirPath, "script.js", result.getJsCode());
        return new File(baseDirPath);
    }

    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     */
    private static String buildUniqueDir(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_PATH + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入文件
     * @param baseDirPath 文件保存根路径
     * @param fileName 文件名
     * @param fileContent 文件内容
     */
    private static void writeToFile(String baseDirPath, String fileName, String fileContent) {
        String filePath = baseDirPath + File.separator + fileName;
        FileUtil.writeString(fileContent, filePath, StandardCharsets.UTF_8);
    }
}
