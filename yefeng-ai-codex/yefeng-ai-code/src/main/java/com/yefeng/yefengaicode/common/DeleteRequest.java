package com.yefeng.yefengaicode.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * id
     * 用于标识需要删除的记录ID
     */
    private Long id;
}
