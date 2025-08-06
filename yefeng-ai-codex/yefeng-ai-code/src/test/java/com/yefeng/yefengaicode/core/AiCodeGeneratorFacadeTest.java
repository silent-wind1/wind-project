package com.yefeng.yefengaicode.core;

import com.yefeng.yefengaicode.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@SpringBootTest
class AiCodeGeneratorFacadeTest {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateCodeAndSaveCode() {
        File file = aiCodeGeneratorFacade.generateCodeAndSaveCode("任务记录网站", CodeGenTypeEnum.MULTI_FILE);
        Assertions.assertNotNull(file);
    }

    @Test
    void generateCodeAndSaveCode2() {
        Flux<String> flux = aiCodeGeneratorFacade.generateCodeAndSaveCodeStream("生成一个二次元登录页面", CodeGenTypeEnum.MULTI_FILE);
        List<String> result = flux.collectList().block();
        Assertions.assertNotNull(result);
        String content = String.join("", result);
        Assertions.assertNotNull(content);
    }
}