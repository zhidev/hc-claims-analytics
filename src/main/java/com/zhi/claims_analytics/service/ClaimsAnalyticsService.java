package com.zhi.claims_analytics.service;

import com.zhi.claims_analytics.dto.ClaimsByStateDTO;
import com.zhi.claims_analytics.dto.ClaimsByStatusDTO;
import com.zhi.claims_analytics.dto.ClaimsTotalByStateDTO;
import com.zhi.claims_analytics.dto.TopProviderDTO;
import com.zhi.claims_analytics.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public List<ClaimsByStatusDTO> getClaimsByStatus() {
        return claimRepository.countClaimsByStatus()
                .stream()
                .map(row -> new ClaimsByStatusDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                )).toList();

    }

    public List<ClaimsTotalByStateDTO> getTotalClaimAmountByState(){
        return claimRepository.sumClaimAmountByStatus()
                .stream()
                .map( row -> new ClaimsTotalByStateDTO(
                        (String) row[0],
                        (BigDecimal) row[1]
                )).toList();
    }

    public List<TopProviderDTO> findTopProviders(){
        return claimRepository.findTopProviders()
                .stream()
                .map( row -> new TopProviderDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                )).toList();
    }
}
