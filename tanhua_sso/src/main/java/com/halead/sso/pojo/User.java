package com.halead.sso.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @title: User
 * @Author ppjjss
 * @Date: 2022/6/28 20:14
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BasePojo{

    private Long id;

    private String mobile;
    @JsonIgnore
    private String password;
}
