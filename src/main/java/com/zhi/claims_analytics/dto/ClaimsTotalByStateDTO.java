package com.zhi.claims_analytics.dto;

import java.math.BigDecimal;

public class ClaimsTotalByStateDTO {
    private String state;
    private BigDecimal totalClaimAmount;

    public ClaimsTotalByStateDTO(String state, BigDecimal totalClaimAmount){
        this.state = state;
        this.totalClaimAmount = totalClaimAmount;
    }

    public String getState() {
        return state;
    }

    public BigDecimal getTotalClaimAmount() {
        return totalClaimAmount;
    }
}
