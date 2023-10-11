package com.ich.note.exception;

import lombok.Getter;

/**
 * @className: ServiceRollbackException
 * @Description: 业务异常
 * @Author: ich
 */
@Getter
public class ServiceRollbackException extends ServiceException {

    public ServiceRollbackException(String message, String code) {
        super(message, code);
    }
}
