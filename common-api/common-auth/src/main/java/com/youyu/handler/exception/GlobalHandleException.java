package com.youyu.handler.exception;

import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalHandleException {


    @ExceptionHandler(SystemException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult systemExceptionHandler(SystemException ex) {
        log.error("出现异常!SystemException: {}", ex);
        return ResponseResult.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseResult authenticationExceptionHandler(AuthenticationException ex) {
        log.error("出现异常!AuthenticationException: {}", ex);
        return ResponseResult.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult argumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.error("出现异常!MethodArgumentNotValidException: {}", ex);
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String specific = fieldErrorList.get(0).getDefaultMessage();
        return ResponseResult.error(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), specific);
    }

    @ExceptionHandler(BindException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult bindExceptionHandler(BindException ex) {
        log.error("出现异常!BindException: {}", ex);
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String specific = fieldErrorList.get(0).getDefaultMessage();
        return ResponseResult.error(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), specific);
    }

    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult bindExceptionHandler(ConstraintViolationException ex) {
        log.error("出现异常!ConstraintViolationException: {}", ex);
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        List<String> collect = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        String message = String.join(",", collect);
        return ResponseResult.error(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult accessDeniedExceptionHandler(AccessDeniedException ex, HttpServletResponse response) {
        log.error("出现异常!SystemException: {}", ex);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResponseResult.error(ResultCode.UNAUTHORIZED.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult exceptionHandler(Exception ex) {
        log.error("出现异常!Exception: {}", ex);
        return ResponseResult.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage());
    }
}
