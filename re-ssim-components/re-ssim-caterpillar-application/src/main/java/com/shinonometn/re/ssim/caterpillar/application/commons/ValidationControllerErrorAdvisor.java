package com.shinonometn.re.ssim.caterpillar.application.commons;

import com.shinonometn.re.ssim.commons.validation.exception.ValidationException;
import com.shiononometn.commons.web.RexModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidationControllerErrorAdvisor {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<RexModel<Object>> validationException(ValidationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(RexModel
                        .error(exception.getError())
                        .withMessage(exception.getMessage())
                        .withData(exception.getResult().getMessages()));
    }
}
