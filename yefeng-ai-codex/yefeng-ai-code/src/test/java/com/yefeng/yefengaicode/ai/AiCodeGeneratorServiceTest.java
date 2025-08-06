package com.yefeng.yefengaicode.ai;

import com.yefeng.yefengaicode.ai.model.HtmlCodeResult;
import com.yefeng.yefengaicode.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode("做个工作记录小工具");
        Assertions.assertNotNull(result);
    }

    @Test
    void testGenerateHtmlCode() {
        MultiFileCodeResult multiFileCode = aiCodeGeneratorService.generateMultiFileCode("做个留言板");
        Assertions.assertNotNull(multiFileCode);
    }

    @Test
    void generateHtmlCodeStream() {
        Flux<String> flux = aiCodeGeneratorService.generateMultiFileCodeStream("做个工作记录小工具");
        List<String> result = flux.collectList().block();
        Assertions.assertNotNull(result);
        String content = String.join("", result);
        Assertions.assertNotNull(content);
    }
}