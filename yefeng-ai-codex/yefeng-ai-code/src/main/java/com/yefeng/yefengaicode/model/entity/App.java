package com.yefeng.yefengaicode.model.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用 实体类。
 *
 * @author yefeng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table("app")
public class App implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 应用名称
     */
    @Column("appName")
    @ExcelProperty(value = "应用名称", index = 0)
    private String appName;

    /**
     * 应用封面
     */
    @ExcelProperty(value = "应用封面", index = 1)
    private String cover;

    /**
     * 应用初始化的 prompt
     */
    @Column("initPrompt")
    @ExcelProperty(value = "应用初始化的prompt", index = 2)
    private String initPrompt;

    /**
     * 代码生成类型（枚举）
     */
    @Column("codeGenType")
    @ExcelProperty(value = "代码生成类型", index = 3)
    private String codeGenType;

    /**
     * 部署标识
     */
    @Column("deployKey")
    private String deployKey;

    /**
     * 部署时间
     */
    @Column("deployedTime")
    @ExcelProperty(value = "部署时间", index = 4)
    private LocalDateTime deployedTime;

    /**
     * 优先级
     */
    @Column("priority")
    @ExcelProperty(value = "优先级", index = 5)
    private Integer priority;

    /**
     * 创建用户id
     */
    @Column("userId")
    private Long userId;

    /**
     * 编辑时间
     */
    @Column("editTime")
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

}
