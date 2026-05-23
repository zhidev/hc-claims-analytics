package com.zhi.claims_analytics.dto;

public class ClaimsByStateDTO {
    private String state;
    private Long claimCount;

    public ClaimsByStateDTO(String state, Long claimCount) {
        this.state = state;
        this.claimCount = claimCount;
    }

    public String getState() {
        return state;
    }

    public Long getClaimCount() {
        return claimCount;
    }
}
