package com.versoaltima.task.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleXMLProcessingException(Exception e) {
        log.error("Exception caught -> ", e);
        return ResponseEntity.internalServerError().body(ExceptionUtils.getRootCauseMessage(e));
    }
}
