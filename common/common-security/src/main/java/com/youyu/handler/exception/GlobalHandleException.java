package com.youyu.handler.exception;

import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalHandleException {

    @Value("${spring.profiles.active}")
    private String active;

    public String getErrorMsg(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(SystemException.class)
    public ResponseResult<?> systemExceptionHandler(SystemException ex) {
        log.error("出现异常!SystemException: {}", getErrorMsg(ex), ex);
        return ResponseResult.error(ex.getCode(), getErrorMsg(ex));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseResult<?> authenticationExceptionHandler(AuthenticationException ex) {
        log.error("出现异常!AuthenticationException: {}", getErrorMsg(ex), ex);
        return ResponseResult.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), getErrorMsg(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<?> argumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        // log.error("出现异常!MethodArgumentNotValidException: {}", getErrorMsg(ex), ex);
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String specific = fieldErrorList.get(0).getDefaultMessage();
        return ResponseResult.error(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), specific);
    }

    @ExceptionHandler(BindException.class)
    public ResponseResult<?> bindExceptionHandler(BindException ex) {
        // log.error("出现异常!BindException: {}", getErrorMsg(ex), ex);
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String specific = fieldErrorList.get(0).getDefaultMessage();
        return ResponseResult.error(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), specific);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult<?> bindExceptionHandler(ConstraintViolationException ex) {
        // log.error("出现异常!ConstraintViolationException: {}", getErrorMsg(ex), ex);
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        List<String> collect = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        String message = String.join(",", collect);
        return ResponseResult.error(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseResult<?> accessDeniedExceptionHandler(AccessDeniedException ex, HttpServletResponse response) {
        // log.error("出现异常!SystemException: {}", getErrorMsg(ex), ex);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return ResponseResult.error(ResultCode.FORBIDDEN.getCode(), getErrorMsg(ex));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<?> exceptionHandler(Exception ex) {
        log.error("出现异常!Exception: {}", getErrorMsg(ex), ex);
        return ResponseResult.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), getErrorMsg(ex));
    }
}
