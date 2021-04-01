package com.touchspring.ailge.exception;

/**
 * @program: agile
 * @description:
 * @author: zdj
 * @create: 2020-10-30 09:51
 **/
public class ExcelHeadException extends RuntimeException{

    public ExcelHeadException(String message) {
        this(message, null);
    }

    public ExcelHeadException(String message, Throwable cause) {
        super(message, cause);
    }


}
