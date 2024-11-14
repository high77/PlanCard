package com.ssafy.backend.domain.plan.service;



import com.ssafy.backend.domain.plan.dto.MyPlanResponseDto;
import com.ssafy.backend.domain.plan.dto.PlanCreateRequestDto;
import com.ssafy.backend.domain.plan.dto.PlanDateUpdateRequestDto;
import com.ssafy.backend.domain.plan.dto.PlanNameUpdateRequestDto;
import com.ssafy.backend.domain.plan.entity.Plan;
import com.ssafy.backend.domain.plan.exception.PlanError;
import com.ssafy.backend.domain.plan.exception.PlanException;
import com.ssafy.backend.domain.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public Long createPlan(PlanCreateRequestDto createRequestDto) {
        Plan plan = planRepository.save(createRequestDto.toEntity());
        return plan.getId();
    }

    @Override
    public void updatePlanName(Long planId, PlanNameUpdateRequestDto planNameUpdateRequestDto) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new PlanException(PlanError.NOT_FOUND_PLAN));
        plan.updateName(planNameUpdateRequestDto.getName());
        planRepository.save(plan);

    }

    @Override
    public void updatePlanDate(Long planId, PlanDateUpdateRequestDto planDateUpdateRequestDto) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new PlanException(PlanError.NOT_FOUND_PLAN));
        plan.updateStartDate(planDateUpdateRequestDto.getStartDate());
        plan.updateEndDate(planDateUpdateRequestDto.getEndDate());
        planRepository.save(plan);

    }

    @Override
    public List<MyPlanResponseDto> getMyPlanList(Long memberId) {
        List<Plan> plans = planRepository.findByPlanMembers_MemberId(memberId);
        return plans.stream()
                .map(plan -> new MyPlanResponseDto(
                        plan.getId(),
                        plan.getName(),
                        plan.getStartDate(),
                        plan.getEndDate(),
                        plan.getPlanMembers().size(),
                        getPlanMembersName(plan)))
                .collect(Collectors.toList());
    }

    @Override
    public MyPlanResponseDto getMyPlan(Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow();
        MyPlanResponseDto myPlan =  new MyPlanResponseDto(
                plan.getId(),
                plan.getName(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getPlanMembers().size(),
                getPlanMembersName(plan));
        return myPlan;
    }


    private List<String> getPlanMembersName(Plan plan) {
        return plan.getPlanMembers().stream()
                .map(planMember -> planMember.getMember().getName())
                .collect(Collectors.toList());
    }
}
