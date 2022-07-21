package com.halead.server.controller;

import com.halead.server.pojo.User;
import com.halead.server.utils.UserThreadLocal;
import com.halead.sso.vo.HuanXinUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: HuanxinController
 * @Author ppjjss
 * @Date: 2022/7/3 19:44
 * @Version 1.0
 */
@RestController
@RequestMapping("huanxin")
public class HuanxinController {

    @GetMapping("user")
    public ResponseEntity<HuanXinUser> queryUser(){
        User user = UserThreadLocal.get();

        HuanXinUser huanXinUser = new HuanXinUser();
        huanXinUser.setUsername(user.getId().toString());
        huanXinUser.setPassword(DigestUtils.md5Hex(user.getId()+"_tan_ppjjss"));

        return ResponseEntity.ok(huanXinUser);
    }


}
