package com.halead.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @title: MessageAnnouncement
 * @Author ppjjss
 * @Date: 2022/7/3 23:18
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAnnouncement {

    private String id;
    private String title;
    private String description;
    private String createDate;

}
