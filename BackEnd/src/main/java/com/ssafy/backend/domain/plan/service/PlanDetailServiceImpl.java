package com.ssafy.backend.domain.plan.service;

import com.ssafy.backend.domain.card.entity.Card;
import com.ssafy.backend.domain.card.repository.CardRepository;
import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.member.exception.MemberError;
import com.ssafy.backend.domain.member.exception.MemberException;
import com.ssafy.backend.domain.plan.entity.Plan;
import com.ssafy.backend.domain.plan.exception.PlanError;
import com.ssafy.backend.domain.plan.exception.PlanException;
import com.ssafy.backend.domain.plan.repository.PlanRepository;
import com.ssafy.backend.domain.plan.dto.PlanDetailCreateRequestDto;
import com.ssafy.backend.domain.plan.dto.PlanDetailListResponseDto;
import com.ssafy.backend.domain.plan.entity.PlanDetail;
import com.ssafy.backend.domain.plan.repository.PlanDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanDetailServiceImpl implements PlanDetailService {

    private final PlanRepository planRepository;
    private final CardRepository cardRepository;
    private final PlanDetailRepository planDetailRepository;

    // Map을 활용한 상세 계획 업데이트 최적화
    @Override
    public void updatePlanDetail(Long planId,
                                          List<PlanDetailCreateRequestDto> planDetailCreateRequestDtoList) {
        if (planDetailCreateRequestDtoList == null || planDetailCreateRequestDtoList.isEmpty()) {
            throw new PlanException(PlanError.NOT_FOUND_PLAN_DETAIL);
        }
        Plan plan = planRepository.findById(planId).orElseThrow();
        Map<Long, PlanDetail> existingPlanDetails = planDetailRepository.findByPlanId(planId)
                .stream()
                .collect(Collectors.toMap(PlanDetail::getId, planDetail -> planDetail));
        for (PlanDetailCreateRequestDto newPlanDetail : planDetailCreateRequestDtoList) {
            if (newPlanDetail.getId() == null) {
                Card card = cardRepository.findById(newPlanDetail.getCardId()).orElseThrow();
                planDetailRepository.save(newPlanDetail.toEntity(card, plan));
            } else {
                PlanDetail planDetail = planDetailRepository.findById(newPlanDetail.getId()).orElseThrow();
                planDetail.update(newPlanDetail.getOrderNumber(), newPlanDetail.getDay());
                existingPlanDetails.remove(newPlanDetail.getId());
            }
        }
        planDetailRepository.deleteAll(existingPlanDetails.values());
    }

    @Override
    public List<PlanDetailListResponseDto> getPlanDetailList(Long planId) {
        List<PlanDetail> planDetails = planDetailRepository.findByPlanId(planId);

        List<PlanDetailListResponseDto> sortedPlanDetailList = planDetails.stream()
                .map(planDetail -> new PlanDetailListResponseDto(
                        planDetail.getId(),
                        planDetail.getCard().getId(),
                        planDetail.getCard().getPosition(),
                        planDetail.getCard().getMemo(),
                        planDetail.getCard().getPlace().getName(),
                        planDetail.getCard().getPlace().getAddress(),
                        planDetail.getCard().getPlace().getImage(),
                        planDetail.getCard().getPlace().getLatitude(),
                        planDetail.getCard().getPlace().getLogitude(),
                        planDetail.getOrderNumber(),
                        planDetail.getDay()))
                .sorted() // Comparable에 따라 정렬
                .collect(Collectors.toList());

        return sortedPlanDetailList;
    }
}
