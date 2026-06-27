package com.cccz.common.vo;

import lombok.Data;

/**
 * 统一返回体
 */
@Data
public class Result<T> {

    /** 成功码 */
    public static final int SUCCESS_CODE = 0;
    /** 失败码 */
    public static final int FAIL_CODE = -1;
    /** 成功消息 */
    public static final String SUCCESS_MSG = "success";

    private Integer code;
    private String message;
    private T data;

    private Result() {
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }

    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, SUCCESS_MSG, null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(FAIL_CODE, message, null);
    }
}
