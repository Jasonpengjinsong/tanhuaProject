package com.halead.dubbo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @title: PageInfo
 * @Author ppjjss
 * @Date: 2022/6/29 22:59
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo<T> implements Serializable {

    private static final long serializableUID = -2105385689859184204L;

    private Integer total; //总条数

    private Integer pageNum; //当前页

    private Integer pageSize; //一页显示的大小

    private List<T> records = Collections.emptyList(); //数据列表
}
