package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {
    //自定义异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handlerException(BusinessException be){
        be.printStackTrace();
        ErrorResult errorResult = be.getErrorResult();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }
    //全局异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity Exception(Exception be){
        be.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
    }
}
