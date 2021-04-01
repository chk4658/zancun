package com.touchspring.ailge.exception;

/**
 * @program: agile
 * @description:
 * @author: zdj
 * @create: 2020-10-30 10:20
 **/
public class ExcelFormatException extends RuntimeException{

    public ExcelFormatException(String message) {
        this(message, null);
    }

    public ExcelFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
