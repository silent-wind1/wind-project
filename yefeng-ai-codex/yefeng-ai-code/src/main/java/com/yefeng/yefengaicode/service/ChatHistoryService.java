package com.yefeng.yefengaicode.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yefeng.yefengaicode.model.dto.history.ChatHistoryQueryRequest;
import com.yefeng.yefengaicode.model.entity.ChatHistory;
import com.yefeng.yefengaicode.model.entity.User;

import java.time.LocalDateTime;


/**
 * 对话历史 服务层。
 *
 * @author yefeng
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    boolean deleteByAppId(Long appId);

    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);
}
