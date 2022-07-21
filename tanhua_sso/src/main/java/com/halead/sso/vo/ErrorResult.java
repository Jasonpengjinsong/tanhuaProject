package com.halead.sso.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @title: ErrorResult
 * @Author ppjjss
 * @Date: 2022/6/28 23:59
 * @Version 1.0
 */
@Data
@Builder
public class ErrorResult {

    private String errCode;

    private String errMessage;

}
