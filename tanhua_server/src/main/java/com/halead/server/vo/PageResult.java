package com.halead.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @title: PageResult
 * @Author ppjjss
 * @Date: 2022/6/30 19:24
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult {

    private Integer counts = 0; //总记录数
    private Integer pageSize; //页大小
    private Integer pages; //总页数
    private Integer page; //当前页码
    private List<?> items = Collections.emptyList(); //列表
}
