package io.github.planegg.allone.starter.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据表通用字段
 */
public class CommonColumn implements Serializable {

    /**
     * 主键
     */
    @TableId("${allone.entity.com-col-name.id}")
    private Long id;

    /**
     * 创建人
     */
    @TableField(value = "${allone.entity.com-col-name.create-user}",fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(value = "${allone.entity.com-col-name.create-time}",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField(value = "${allone.entity.com-col-name.update-user}",fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(value = "${allone.entity.com-col-name.update-time}",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 租户ID
     */
    @TableField(value = "${allone.entity.com-col-name.tenant-id}",fill = FieldFill.INSERT)
    private Long tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
