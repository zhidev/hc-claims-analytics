package com.zhi.claims_analytics.service;

import com.zhi.claims_analytics.model.Claim;
import com.zhi.claims_analytics.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimsService {

    //Handles general claim operations
    private final ClaimRepository claimRepository;

    public ClaimsService(ClaimRepository claimRepository){
        this.claimRepository = claimRepository;
    }

    public List<Claim> getAllClaims(){
        return claimRepository.findAll();
    }
}
