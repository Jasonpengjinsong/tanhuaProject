package com.halead.server.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @title: User
 * @Author ppjjss
 * @Date: 2022/6/30 18:12
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BasePojo{

    private Long id;

    private String mobile;
    @JsonIgnore
    private String password;
}
