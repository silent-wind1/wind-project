package com.yefeng.yefengaicode.core;

import com.yefeng.yefengaicode.ai.AiCodeGeneratorService;
import com.yefeng.yefengaicode.ai.model.HtmlCodeResult;
import com.yefeng.yefengaicode.ai.model.MultiFileCodeResult;
import com.yefeng.yefengaicode.core.parser.CodeParserExecutor;
import com.yefeng.yefengaicode.core.saver.CodeFileSaverExecutor;
import com.yefeng.yefengaicode.exception.BusinessException;
import com.yefeng.yefengaicode.exception.ErrorCode;
import com.yefeng.yefengaicode.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成器门面
 */
@Slf4j
@Service
public class AiCodeGeneratorFacade {
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateCodeAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "请选择代码生成类型");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage);
            default -> {
                String msg = "不支持的代码生成类型" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, msg);
            }
        };
    }

    /**
     * 统一入口：根据类型生成并保存代码（流式）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public Flux<String> generateCodeAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "请选择代码生成类型");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCodeStream(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCodeStream(userMessage);
            default -> {
                String msg = "不支持的代码生成类型" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, msg);
            }
        };
    }

    /**
     * 根据类型生成并保存代码
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
        Flux<String> flux = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        StringBuffer codeBuild = new StringBuffer();
        return flux.doOnNext(item -> {
            codeBuild.append(item);
        }).doOnComplete(() -> {
            try {
                String htmlCode = codeBuild.toString();
                MultiFileCodeResult multiFileResult = CodeParser.parseMultiFileCode(htmlCode);
                File file = CodeFileSaver.saveMultiFileCodeResult(multiFileResult);
                log.info("保存的目录:{}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存代码失败", e);
            }
        }).doOnComplete(() -> {
            try {
                String htmlCode = codeBuild.toString();
                MultiFileCodeResult multiFileResult = CodeParser.parseMultiFileCode(htmlCode);
                File file = CodeFileSaver.saveMultiFileCodeResult(multiFileResult);
                log.info("保存成功，路径为：{}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存代码失败： {}", e.getMessage());
            }
        });
    }

    /**
     * 生成HTML代码流式
     *
     * @param userMessage 用户提示词
     * @return HTML代码
     */
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
        Flux<String> flux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        StringBuffer codeBuild = new StringBuffer();
        return flux.doOnNext(codeBuild::append).doOnComplete(() -> {
            try {
                String htmlCode = codeBuild.toString();
                HtmlCodeResult codeFileResult = CodeParser.parseHtmlCode(htmlCode);
                File file = CodeFileSaver.saveCodeHtmlCodeFile(codeFileResult);
                log.info("保存成功，路径为：{}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存HTML代码失败： {}", e.getMessage());
            }
        });
    }


    /**
     * 生成多个文件并保存代码
     *
     * @param userMessage 用户提示词
     * @return 文件目录
     */
    private File generateAndSaveMultiFileCode(String userMessage) {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(result);
    }

    /**
     * 生成并保存HTML代码
     *
     * @param userMessage 用户提示词
     * @return 文件目录
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveCodeHtmlCodeFile(result);
    }


    /**
     * 通用流式代码处理方法
     *
     * @param codeStream  代码流
     * @param codeGenType 代码生成类型
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType) {
        StringBuilder codeBuilder = new StringBuilder();
        // 实时收集代码片段
        return codeStream.doOnNext(codeBuilder::append).doOnComplete(() -> {
            // 流式返回完成后保存代码
            try {
                String completeCode = codeBuilder.toString();
                // 使用执行器解析代码
                Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
                // 使用执行器保存代码
                File savedDir = CodeFileSaverExecutor.executeSaver(parsedResult, codeGenType);
                log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存失败: {}", e.getMessage());
            }
        });
    }

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.MULTI_FILE);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 统一入口：根据类型生成并保存代码（流式）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML);
            }
            case MULTI_FILE -> {
                Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }
}
