package com.halead.server.controller;

import com.halead.server.service.TodayBestService;
import com.halead.server.vo.TodayBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: TodayBestController
 * @Author ppjjss
 * @Date: 2022/6/30 18:18
 * @Version 1.0
 */
@RestController
@RequestMapping("tanhua")
public class TodayBestController {


    @Autowired
    private TodayBestService todayBestService;

    /**
     * 查询今日佳人
     * @param userId
     * @return
     */
    @GetMapping("{id}/personalInfo")
    public ResponseEntity<TodayBest> queryTodayBest(@PathVariable("id") Long userId){

        try {
            TodayBest todayBest= this.todayBestService.queryTodayBest(userId);
            return ResponseEntity.ok(todayBest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
