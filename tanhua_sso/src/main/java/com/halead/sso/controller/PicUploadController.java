package com.halead.sso.controller;

import com.halead.sso.service.PicUploadService;
import com.halead.sso.vo.PicUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @title: PicUploadController
 * @Author ppjjss
 * @Date: 2022/6/29 20:20
 * @Version 1.0
 */
@RequestMapping("pic/upload")
@Controller
public class PicUploadController {

    @Autowired
    private PicUploadService picUploadService;

    @PostMapping
    @ResponseBody
    public PicUploadResult upload(@RequestParam("file")MultipartFile multipartFile){
        return  this.picUploadService.upload(multipartFile);
    }

}
