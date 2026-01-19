package com.zhufuyu.bless.exception;

import com.zhufuyu.bless.model.common.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ResponseEntity<BaseResponse<Void>> handleBizException(BizException ex) {
        BaseResponse<Void> body = BaseResponse.error(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("参数校验失败");
        BaseResponse<Void> body = BaseResponse.error(10010, message);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleOtherException(Exception ex) {
        log.error("Unexpected error", ex);
        BaseResponse<Void> body = BaseResponse.error(50000, "系统繁忙，请稍后重试");
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
