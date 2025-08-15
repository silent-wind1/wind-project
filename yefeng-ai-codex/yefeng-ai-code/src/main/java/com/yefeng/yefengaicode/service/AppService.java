package com.yefeng.yefengaicode.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yefeng.yefengaicode.model.dto.app.AppQueryRequest;
import com.yefeng.yefengaicode.model.entity.App;
import com.yefeng.yefengaicode.model.vo.AppVO;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author yefeng
 */
public interface AppService extends IService<App> {

    AppVO getAppVO(App app);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> appList);
}
