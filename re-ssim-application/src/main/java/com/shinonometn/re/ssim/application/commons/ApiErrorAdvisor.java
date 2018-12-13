package com.shinonometn.re.ssim.application.commons;

import com.shinonometn.re.ssim.commons.validation.ValidationException;
import com.shiononometn.commons.web.RexModel;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiErrorAdvisor {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<RexModel> validationException(ValidationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(RexModel
                        .error(exception.getError())
                        .withMessage(exception.getMessage())
                        .withData(exception.getResult().getMessages()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RexModel> authenticationException(AuthenticationException exception) {
        return ResponseEntity.ok(RexModel.error("authentication_error").withMessage(exception.getMessage()));
    }
}
