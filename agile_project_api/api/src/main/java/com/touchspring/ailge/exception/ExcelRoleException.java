package com.touchspring.ailge.exception;

/**
 * @program: agile
 * @description:
 * @author: zdj
 * @create: 2020-10-30 10:15
 **/
public class ExcelRoleException extends RuntimeException{

    public ExcelRoleException(String message) {
        this(message, null);
    }

    public ExcelRoleException(String message, Throwable cause) {
        super(message, cause);
    }

}
