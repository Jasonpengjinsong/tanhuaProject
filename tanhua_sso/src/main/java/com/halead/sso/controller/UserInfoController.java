package com.halead.sso.controller;

import com.halead.sso.service.UserInfoService;
import com.halead.sso.vo.ErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @title: UserInfoController
 * @Author ppjjss
 * @Date: 2022/6/29 21:51
 * @Version 1.0
 */
@RestController
@RequestMapping("user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 完善个人信息
     */
    @RequestMapping("loginReginfo")
    @PostMapping
    public ResponseEntity<Object> saveUserInfo(@RequestBody Map<String,String> param,@RequestHeader("Authorization") String token){

        try {
            boolean saveUserInfo = this.userInfoService.saveUserInfo(param, token);
            if(saveUserInfo){
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ErrorResult errorResult = ErrorResult.builder().errCode("000000").errMessage("发生错误").build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    @RequestMapping("loginReginfo/head")
    @PostMapping
    public ResponseEntity<Object> saveLogo(@RequestParam("headPhoto")MultipartFile file,@RequestHeader("Authorization") String token){
        try {
            Boolean bool = this.userInfoService.saveLogo(file, token);
            if(bool){
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ErrorResult errorResult = ErrorResult.builder().errCode("000000").errMessage("图片上传非人像，请重新上传").build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);

    }

}
