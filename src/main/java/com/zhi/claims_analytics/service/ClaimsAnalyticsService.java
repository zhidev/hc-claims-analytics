package com.zhi.claims_analytics.service;

import com.zhi.claims_analytics.ClaimsAnalyticsApplication;
import com.zhi.claims_analytics.dto.ClaimsByStateDTO;
import com.zhi.claims_analytics.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimsAnalyticsService {
    private final ClaimRepository claimRepository;

    public ClaimsAnalyticsService(ClaimRepository claimRepository){
        this.claimRepository = claimRepository;
    }

    public List<ClaimsByStateDTO> getClaimsByState(){

        return claimRepository.countClaimsByState()
                .stream()
                .map(row -> new ClaimsByStateDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                )).toList();
    }
}
