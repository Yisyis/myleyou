package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *  全局异常统一处理
 *   @ControllerAdvice  控制器增强
 *   作用在所有注解了@RequestMapping的控制器的方法上
 */
@ControllerAdvice   // 控制器增强
public class CommonExceptionHandler {

    @ExceptionHandler(LyException.class)  // 用于全局处理控制器里的LyException异常
    public ResponseEntity<ExceptionResult> handleException(LyException e) {
        return ResponseEntity.status(e.getExceptionEnum().getCode()).body(new ExceptionResult(e.getExceptionEnum()));
    }

}
