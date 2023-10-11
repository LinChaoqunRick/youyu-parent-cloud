package com.youyu.handler.exception;

import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalHandleException {

    @ExceptionHandler(SystemException.class)
    public ResponseResult systemExceptionHandler(SystemException ex) {
        log.error("出现异常!SystemException {}", ex);
        return ResponseResult.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult argumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.error("出现异常!SystemException {}", ex);
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String specific = fieldErrorList.get(0).getDefaultMessage();
        return ResponseResult.error(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), specific);
    }

    @ExceptionHandler(BindException.class)
    public ResponseResult bindExceptionHandler(BindException ex) {
        log.error("出现异常!SystemException {}", ex);
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String specific = fieldErrorList.get(0).getDefaultMessage();
        return ResponseResult.error(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), specific);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult bindExceptionHandler(ConstraintViolationException ex) {
        log.error("出现异常!SystemException {}", ex);
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        List<String> collect = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        String message = String.join(",", collect);
        return ResponseResult.error(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult exceptionHandler(Exception ex) {
        log.error("出现异常!Exception {}", ex);
        return ResponseResult.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage());
    }
}
