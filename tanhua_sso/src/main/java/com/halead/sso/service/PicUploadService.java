package com.halead.sso.service;

import com.aliyun.oss.OSSClient;
import com.halead.sso.config.AliyunConfig;
import com.halead.sso.vo.PicUploadResult;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @title: PicUploadService
 * @Author ppjjss
 * @Date: 2022/6/29 19:50
 * @Version 1.0
 */
@Service
public class PicUploadService {


    @Autowired
    private OSSClient ossClient;

    @Autowired
    private AliyunConfig aliyunConfig;

    //允许上传的格式
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg", ".jpeg", ".gif", ".png"};

    public PicUploadResult upload(MultipartFile uploadFile) {
        PicUploadResult picUploadResult = new PicUploadResult();
        //对图片的格式进行校验
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(), type)) {
                isLegal = true;
                break;
            }
        }
        if (!isLegal) {
            picUploadResult.setStatus("error");
            return picUploadResult;
        }
        //设置图片的新路径 images/2018/12/29/xxxx.jpg
        String fileName = uploadFile.getOriginalFilename();
        String filePath = getFilePath(fileName);
        //上传图片
        try {
            ossClient.putObject(aliyunConfig.getBucketName(), filePath, new ByteArrayInputStream(uploadFile.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
            picUploadResult.setStatus("errot");
            return picUploadResult;
        }
        picUploadResult.setStatus("done");
        picUploadResult.setName(this.aliyunConfig.getUrlPrefix()+filePath);
        picUploadResult.setUid(String.valueOf(System.currentTimeMillis()));
        return picUploadResult;
    }

    private String getFilePath(String fileName) {
        DateTime dateTime = new DateTime();

        return "images/" + dateTime.toString("yyyy") + "/" + dateTime.toString("MM") + "/" + dateTime.toString("dd") + "/" + System.currentTimeMillis() + RandomUtils.nextInt(100, 9999)
                + "." + StringUtils.substringAfterLast(fileName, ".");
    }

}
