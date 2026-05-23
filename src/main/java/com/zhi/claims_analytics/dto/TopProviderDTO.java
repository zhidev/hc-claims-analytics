package com.zhi.claims_analytics.dto;

public class TopProviderDTO {
    String providerId;
    Long claimCount;

    public TopProviderDTO(String providerId, Long claimCount){
        this.providerId = providerId;
        this.claimCount = claimCount;
    }

    public String getProviderId() {
        return providerId;
    }

    public Long getClaimCount() {
        return claimCount;
    }
}
