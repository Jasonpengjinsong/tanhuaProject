package com.halead.sso.vo;

import lombok.Data;

/**
 * @title: PicUploadResult
 * @Author ppjjss
 * @Date: 2022/6/29 19:52
 * @Version 1.0
 */
@Data
public class PicUploadResult {

    private String uid; //文件的唯一表示

    private String name; //文件名

    private String status; //状态有：uploading done error removed

    private String response; // 服务端响应内容，如：'{"status": "success"}'
}
