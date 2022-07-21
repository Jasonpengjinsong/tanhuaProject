package com.halead.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @title: MessageLike
 * @Author ppjjss
 * @Date: 2022/7/3 22:12
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageLike {

    private String id;
    private String avatar;
    private String nickname;
    private String createDate;

}
