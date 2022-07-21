package com.halead.sso;

import com.halead.sso.service.SmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @title: TestSmsService
 * @Author ppjjss
 * @Date: 2022/6/28 23:55
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSmsService {

    @Autowired
    private SmsService smsService;
    @Test
    public void sendSms(){
        this.smsService.sendSms("13646738476");
    }

}
