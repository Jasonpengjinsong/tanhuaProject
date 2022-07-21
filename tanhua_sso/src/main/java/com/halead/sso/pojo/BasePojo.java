package com.halead.sso.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

/**
 * @title: BasePojo
 * @Author ppjjss
 * @Date: 2022/6/28 20:12
 * @Version 1.0
 */
public abstract class BasePojo {
    @TableField(fill = FieldFill.INSERT)
    private Date created;
    @TableField(fill = FieldFill.UPDATE)
    private Date update;
}
