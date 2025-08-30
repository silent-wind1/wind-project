package com.yefeng.yefengaicode;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.yefeng.yefengaicode.listener.ExcelListener;
import com.yefeng.yefengaicode.model.entity.App;
import com.yefeng.yefengaicode.service.AppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class YefengAiCodeApplicationTests {
    @Resource
    private AppService appService;

    @Test
    void contextLoads() {
        // Excel文件的路径
        String filePath = System.getProperty("user.dir") + "/test.xlsx";
        log.info("当前文件路径：{}", filePath);
        // 使用EasyExcel读取指定的sheet
        EasyExcel.read(filePath, App.class, new ExcelListener(appService)).excelType(ExcelTypeEnum.XLSX).sheet(0).doRead();
    }

}
