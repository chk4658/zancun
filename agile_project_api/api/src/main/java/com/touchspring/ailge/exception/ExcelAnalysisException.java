package com.touchspring.ailge.exception;

/**
 * @program: agile
 * @description:
 * @author: zdj
 * @create: 2020-10-28 18:06
 **/
public class ExcelAnalysisException extends RuntimeException{

    public ExcelAnalysisException(String message) {
        this(message, null);
    }

    public ExcelAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

}
