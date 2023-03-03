package io.github.planegg.allone.starter.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.planegg.allone.starter.entity.CommonColumn;

/**
 * <p>
 * 序列生成表
 * </p>
 *
 * @author 作者
 * @since 2023-01-31
 */
@TableName("ms_serial_generate")
public class MsSerialGenerateEntity extends CommonColumn {

    private static final long serialVersionUID = 1L;

    /**
     * 序列编码，序列唯一标识
     */
    @TableField("serial_code")
    private String serialCode;

    /**
     * 序列最大值：当前已生成序列的最大值
     */
    @TableField("max_num")
    private Long maxNum;

    /**
     * 批次数量，缓存单次获取序列的数量
     */
    @TableField("batch_num")
    private Integer batchNum;

    /**
     * 描述
     */
    @TableField("description")
    private String description;


    public String getSerialCode() {
        return serialCode;
    }

    public MsSerialGenerateEntity setSerialCode(String serialCode) {
        this.serialCode = serialCode;
        return this;
    }

    public Long getMaxNum() {
        return maxNum;
    }

    public MsSerialGenerateEntity setMaxNum(Long maxNum) {
        this.maxNum = maxNum;
        return this;
    }

    public Integer getBatchNum() {
        return batchNum;
    }

    public MsSerialGenerateEntity setBatchNum(Integer batchNum) {
        this.batchNum = batchNum;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MsSerialGenerateEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "MsSerialGenerateEntity{" +
        "serialCode=" + serialCode +
        ", maxNum=" + maxNum +
        ", batchNum=" + batchNum +
        ", description=" + description +
        "}";
    }
}
