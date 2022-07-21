package com.halead.server.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @title: BasePojo
 * @Author ppjjss
 * @Date: 2022/6/30 0:05
 * @Version 1.0
 */
@Data
public class BasePojo {
    @TableField(fill=FieldFill.INSERT)
    private Date created;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date update;
}
