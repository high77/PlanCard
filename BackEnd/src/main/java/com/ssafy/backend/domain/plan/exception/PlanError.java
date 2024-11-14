package com.ssafy.backend.domain.plan.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlanError {

    NOT_FOUND_PLAN_DETAIL(HttpStatus.INTERNAL_SERVER_ERROR, "생성할 여행 상세 계획을 찾을 수 없습니다."),
    NOT_FOUND_PLAN(HttpStatus.INTERNAL_SERVER_ERROR, "여행 계획을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String errorMessage;
}
