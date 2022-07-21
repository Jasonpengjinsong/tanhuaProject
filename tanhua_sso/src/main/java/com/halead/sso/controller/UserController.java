package com.halead.sso.controller;

import com.halead.sso.pojo.User;
import com.halead.sso.service.UserServcie;
import com.halead.sso.vo.ErrorResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @title: UserController
 * @Author ppjjss
 * @Date: 2022/6/29 18:48
 * @Version 1.0
 */
@RequestMapping("user")
@RestController
public class UserController {

    @Autowired
    private UserServcie userServcie;

    /**
     * 登录
     *
     * @param param
     * @return
     */
    @PostMapping("loginVerification")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> param) {
        try {
            String mobile = param.get("phone");
            String code = param.get("loginVerification");
            String tokenAndisNew = this.userServcie.login(mobile, code);
            if (StringUtils.isNoneEmpty(tokenAndisNew)) {
                String[] ss = StringUtils.split(tokenAndisNew, '|');
                Boolean isNew = Boolean.valueOf(ss[0]);
                String tokenStr = ss[1];

                HashMap<String, Object> result = new HashMap<>();
                result.put("isNew", isNew);
                result.put("token", tokenStr);
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //登录失败
        ErrorResult.ErrorResultBuilder builder = ErrorResult.builder().errCode("000000").errMessage("登录失败");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(builder.build());

    }

    /**
     * 根据token查询用户信息
     *
     * @param token
     * @return
     */
    @GetMapping("{token}")
    public User queryUserByToken(@PathVariable("token") String token) {
        return this.userServcie.queryUserByToken(token);
    }
}
