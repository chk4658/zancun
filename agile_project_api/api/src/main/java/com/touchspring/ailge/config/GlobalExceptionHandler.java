package com.touchspring.ailge.config;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.touchspring.core.mvc.status.BaseResultStatus;
import com.touchspring.core.mvc.ui.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * token 过期
     *
     * @param e        .
     * @param response .
     * @return .
     */
    @ExceptionHandler({TokenExpiredException.class, com.touchspring.ailge.exception.TokenExpiredException.class})
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResultData tokenExpiredExceptionHandler(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        return new ResultData(BaseResultStatus.TOKEN_EXPIRED.getCode(), BaseResultStatus.TOKEN_EXPIRED.getMessage());
    }


    /**
     * 拦截错误
     *
     * @param e        .
     * @param response .
     * @return .
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResultData exceptionHandler(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        log.info("requestUrl:{}", request.getRequestURI());
        log.info("RuntimeException error:{0}", e);
        return new ResultData(BaseResultStatus.SERVER_ERROR.getCode(), "Server error,please contact the administrator!");
    }


    /**
     * 拦截错误
     *
     * @param e        .
     * @param response .
     * @return .
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResultData allExceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.info("requestUrl:{}", request.getRequestURI());
        log.info("Exception error:{0}", e);
        return new ResultData(BaseResultStatus.SERVER_ERROR.getCode(), "Server error,please contact the administrator!");
    }


}