package com.halead.sso;

import com.halead.sso.service.FaceEngineService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

/**
 * @title: TestFaceEngineService
 * @Author ppjjss
 * @Date: 2022/6/29 21:25
 * @Version 1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFaceEngineService {

    @Autowired
    private FaceEngineService faceEngineService;

    public void TestCheckIsProtrait(){
        File file = new File("");
        boolean checkIsPortrait = this.faceEngineService.checkIsPortrait(file);
        System.out.println(checkIsPortrait);
    }
}
