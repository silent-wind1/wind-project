package com.yefeng.yefengaicode.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.yefeng.yefengaicode.model.entity.App;
import com.yefeng.yefengaicode.service.AppService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel数据读取监听器 - 支持百万级数据处理
 * 
 * @author wind
 * @description: 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 * @date 2025/8/30 13:21
 */
@Slf4j
public class ExcelListener implements ReadListener<App> {
    // 设置批量处理的数据大小
    private static final int MAX_SIZE = 1000;
    // 用于暂存读取的数据，直到达到批量大小
    private final List<App> appList = new ArrayList<>();

    private final AppService appService;

    public ExcelListener(AppService appService) {
        this.appService = appService;
    }

    @Override
    public void invoke(App app, AnalysisContext analysisContext) {
        log.info("app = {}", app);
        if (!validateData(app)) {
            return;
        }
        // 先默认写死
        app.setUserId(1L);
        appList.add(app);
        // 当达到批量大小时，处理这批数据
        if (appList.size() >= MAX_SIZE) {
            processBatch();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 如果还有未处理的数据，进行处理
        if (!appList.isEmpty()) {
            log.info("处理未处理的数据：{}", appList);
            processBatch();
        }
    }

    private boolean validateData(App app) {
        // 调用mapper方法来检查数据库中是否已存在该数据
        long count = appService.countByAppName(app.getAppName());
        // 如果count为0，表示数据不存在，返回true；否则返回false
        if (count == 0) {
            return true;
        }
        // 在这里实现数据验证逻辑
        log.info("数据验证逻辑：{}", app);
        return false;
    }

    // 处理一批数据的方法，重试次数超过 3，进行异常处理
    private void processBatch() {
        int retryCount = 0;
        // 重试逻辑
        while (retryCount < 3) {
            try {
                // 尝试批量插入
                appService.saveBatchApp(appList, MAX_SIZE);
                // 清空批量数据，以便下一次批量处理
                appList.clear();
                break;
            } catch (Exception e) {
                // 重试计数增加
                retryCount++;
                // 如果重试3次都失败，记录错误日志
                if (retryCount >= 3) {
                    logError(e, appList);
                }
            }
        }
    }

    // 记录错误日志的方法
    private void logError(Exception e, List<App> appList) {
        // 在这里实现错误日志记录逻辑
        // 可以记录异常信息和导致失败的数据
        log.error("重试失败错误日志: {}, 未保存记录：{}", e.getMessage(), appList);
    }
}
