package com.vti.configuration.exception;

import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Log4j2
public class ErrorHandler extends ResponseEntityExceptionHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectWriter objectWriter;

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args,
                LocaleContextHolder.getLocale());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : exception.getFieldErrors()) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            errors.put(field, message);
        }
        String message = getMessage("MethodArgumentNotValidException.message");
        ErrorResponse response = new ErrorResponse(1, message, errors);
        return new ResponseEntity<>(response, headers, status);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> constraint : exception.getConstraintViolations()) {
            String field = constraint.getPropertyPath().toString();
            String value = constraint.getMessage();
            errors.put(field, value);
        }
        String message = getMessage("ConstraintViolationException.message");
        ErrorResponse response = new ErrorResponse(2, message, errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handlerException(Exception exception) {
        String message = getMessage("Exception.message");
        ErrorResponse response = new ErrorResponse(3, message);
        log.error(message, exception);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException exception,
            HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        String message = getMessage(
                "NoHandlerFoundException.message",
                exception.getHttpMethod(), exception.getRequestURL()
        );
        ErrorResponse response = new ErrorResponse(4, message);
        log.error(message, exception);
        return new ResponseEntity<>(response, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        String message = getMessage(
                "HttpRequestMethodNotSupportedException.message",
                exception.getMethod()
        );
        ErrorResponse response = new ErrorResponse(5, message);
        return new ResponseEntity<>(response, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        String message = getMessage(
                "HttpMediaTypeNotSupportedException.message",
                exception.getContentType()
        );
        ErrorResponse response = new ErrorResponse(6, message);
        return new ResponseEntity<>(response, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        String message = getMessage(
                "MissingServletRequestParameterException.message",
                exception.getParameterName(),
                exception.getParameterType()
        );
        ErrorResponse response = new ErrorResponse(7, message);
        return new ResponseEntity<>(response, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception,
            HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        if (exception instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException ex = (MethodArgumentTypeMismatchException) exception;
            Class<?> requiredType = ex.getRequiredType();
            String message = getMessage(
                    "MethodArgumentTypeMismatchException.message",
                    ex.getName(),
                    requiredType == null ? "null" : requiredType.getName()
            );
            ErrorResponse response = new ErrorResponse(8, message);
            return new ResponseEntity<>(response, headers, status);
        }
        return super.handleTypeMismatch(exception, headers, status, request);
    }

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        String message = getMessage("AuthenticationException.message");
        ErrorResponse res = new ErrorResponse(9, message);
        String json = objectWriter.writeValueAsString(res);
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(json);
    }

    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException, ServletException {
        String message = getMessage("AccessDeniedException.message");
        ErrorResponse res = new ErrorResponse(10, message);
        String json = objectWriter.writeValueAsString(res);
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(json);
    }
}
