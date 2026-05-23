package com.zhi.claims_analytics.dto;

public class ClaimsByStatusDTO {
    private String status;
    private Long claimCount;

    public ClaimsByStatusDTO(String status, Long claimCount){
        this.status = status;
        this.claimCount = claimCount;
    }

    public String getStatus() {
        return status;
    }

    public Long getClaimCount() {
        return claimCount;
    }
}
