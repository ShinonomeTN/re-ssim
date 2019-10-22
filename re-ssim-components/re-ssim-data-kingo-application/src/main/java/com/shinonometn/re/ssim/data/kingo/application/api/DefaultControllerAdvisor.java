package com.shinonometn.re.ssim.data.kingo.application.api;

import com.shinonometn.re.ssim.commons.BusinessException;
import com.shiononometn.commons.web.RexModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DefaultControllerAdvisor {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public RexModel businessException(BusinessException e) {
        return RexModel.error(e.getError()).withMessage(e.getMessage());
    }

}
