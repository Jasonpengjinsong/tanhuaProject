package com.halead.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @title: Announcement
 * @Author ppjjss
 * @Date: 2022/7/3 23:10
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends BasePojo {

    private Long id;
    private String title;
    private String description;

}
