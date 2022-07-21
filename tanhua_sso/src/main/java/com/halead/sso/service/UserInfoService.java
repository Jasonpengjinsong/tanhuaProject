package com.halead.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.halead.sso.enums.SexEnum;
import com.halead.sso.mapper.UserInfoMapper;
import com.halead.sso.pojo.User;
import com.halead.sso.pojo.UserInfo;
import com.halead.sso.vo.PicUploadResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @title: UserInfoService
 * @Author ppjjss
 * @Date: 2022/6/29 21:32
 * @Version 1.0
 */
@Service
public class UserInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoService.class);

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserServcie userServcie;

    @Autowired
    private FaceEngineService faceEngineService;

    @Autowired
    private PicUploadService picUploadService;

    /**
     * 完善个人信息
     * @param param
     * @param token
     * @return
     */
    public Boolean saveUserInfo(Map<String,String> param,String token){

        User user= this.userServcie.queryUserByToken(token);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setSex(StringUtils.equals(param.get("gender"),"man") ? SexEnum.MAN:SexEnum.WOMAN);
        userInfo.setNickName(param.get("nickname"));
        userInfo.setBirthday(param.get("birthday"));
        userInfo.setCity(param.get("city"));

        this.userInfoMapper.insert(userInfo);
        return true;

    }

    /**
     * 上传头像
     * @param file
     * @param token
     * @return
     */
    public Boolean saveLogo(MultipartFile file,String token){
        User user = this.userServcie.queryUserByToken(token);
        if(null == user){
            return false;
        }
        try {
            boolean isPortrait = this.faceEngineService.checkIsPortrait(file.getBytes());
            if(isPortrait){
                return false;
            }
        } catch (IOException e) {
            LOGGER.error("检测人像图片出错",e);
            return false;
        }
        PicUploadResult uploadResult = this.picUploadService.upload(file);
        UserInfo userInfo = new UserInfo();
        userInfo.setLogo(uploadResult.getName());
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id",user.getId());
        this.userInfoMapper.update(userInfo,wrapper);
        return true;
    }
}
