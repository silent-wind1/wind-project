package com.yefeng.yefengaicode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yefeng.yefengaicode.constant.AppConstant;
import com.yefeng.yefengaicode.core.AiCodeGeneratorFacade;
import com.yefeng.yefengaicode.exception.BusinessException;
import com.yefeng.yefengaicode.exception.ErrorCode;
import com.yefeng.yefengaicode.exception.ThrowUtils;
import com.yefeng.yefengaicode.mapper.AppMapper;
import com.yefeng.yefengaicode.model.dto.app.AppQueryRequest;
import com.yefeng.yefengaicode.model.entity.App;
import com.yefeng.yefengaicode.model.entity.User;
import com.yefeng.yefengaicode.model.enums.CodeGenTypeEnum;
import com.yefeng.yefengaicode.model.vo.AppVO;
import com.yefeng.yefengaicode.model.vo.UserVO;
import com.yefeng.yefengaicode.service.AppService;
import com.yefeng.yefengaicode.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author yefeng
 */
@Slf4j
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create().eq("id", id).like("appName", appName).like("cover", cover).like("initPrompt", initPrompt).eq("codeGenType", codeGenType).eq("deployKey", deployKey).eq("priority", priority).eq("userId", userId).orderBy(sortField, "ascend".equals(sortOrder));
    }


    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream().map(App::getUserId).collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    /**
     * 生成应用代码
     *
     * @param message   提示词
     * @param appId     应用ID
     * @param loginUser 登录用户
     * @return 代码流
     */
    @Override
    public Flux<String> doToGenCode(Long appId, String message, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(appId == null || appId < 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.PARAMS_ERROR, "用户不能为空");
        // 获取应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        // 权限校验
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.PARAMS_ERROR, "用户无权限操作该应用");
        // 获取应用代码生成类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        // 生成代码
        return aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.PARAMS_ERROR, "用户无权限操作该应用");
        // 检查是否已有 deployKey
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码目录不存在");
        }
        // 复制文件到部署
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 更新
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean result = this.updateById(updateApp);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");
        return String.format("%s/%s", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }

    /**
     * 批量保存 Excel导入数据应用 - 高性能版本
     *
     * @param appList   应用列表
     * @param batchSize 批量大小
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchApp(List<App> appList, int batchSize) {
        if (CollUtil.isEmpty(appList)) {
            log.warn("批量保存应用列表为空，跳过处理");
            return;
        }

        if (batchSize <= 0) {
            batchSize = 1000;
        }
        this.saveBatch(appList, batchSize);
    }

    /**
     * 统计 App 名称数量
     *
     * @param appName 应用名称
     * @return 统计行数
     */
    @Override
    public long countByAppName(String appName) {
        return this.count(QueryWrapper.create().eq("appName", appName));
    }


}
