package com.ich.note.exception;

import lombok.Getter;

/**
 * @className: ServiceException
 * @Description: 业务异常
 * @Author: ich
 */
@Getter
public class ServiceException extends Exception {
    private String code; // 业务状态码

    public ServiceException(String message, String code) {
        super(message);
        this.code = code;
    }
}
