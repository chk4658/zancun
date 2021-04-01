package com.touchspring.ailge.exception;

/**
 * @program: agile
 * @description:
 * @author: zdj
 * @create: 2020-10-30 10:11
 **/
public class ExcelNullException extends RuntimeException{

    public ExcelNullException(String message) {
        this(message, null);
    }

    public ExcelNullException(String message, Throwable cause) {
        super(message, cause);
    }

}
