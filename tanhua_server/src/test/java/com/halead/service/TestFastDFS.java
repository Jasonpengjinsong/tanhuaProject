package com.halead.service;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

/**
 * @title: TestFastDFS
 * @Author ppjjss
 * @Date: 2022/7/3 11:43
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestFastDFS {

    @Autowired
    protected FastFileStorageClient fastFileStorageClient;


    @Test
    public void testUpload(){
        String path ="";
        File file = new File(path);

        StorePath storePath = null;
        try {
            storePath = this.fastFileStorageClient.uploadFile(FileUtils.openInputStream(file), file.length(), "jpg", null);
            System.out.println(storePath);
            System.out.println(storePath.getFullPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
