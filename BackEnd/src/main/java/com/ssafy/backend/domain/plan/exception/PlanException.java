package com.ssafy.backend.domain.plan.exception;

import lombok.Getter;

@Getter
public class PlanException extends RuntimeException {
    private final PlanError errorCode;
    private final int status;
    private final String errorMessage;

    public PlanException(PlanError errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
        this.status = errorCode.getHttpStatus().value();
        this.errorMessage = errorCode.getErrorMessage();
    }
}
