package com.ich.note.exception;

/**
 * @className: ServiceException
 * @Description: 业务异常
 * @Author: ich
 */
public class ValidateParmException extends ServiceException {
    public ValidateParmException(String message, String code) {
        super(message, code);
    }
}
