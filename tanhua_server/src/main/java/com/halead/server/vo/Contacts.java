package com.halead.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @title: Contacts
 * @Author ppjjss
 * @Date: 2022/7/3 20:27
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contacts {

    private Long id;
    private String userId;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String city;

}