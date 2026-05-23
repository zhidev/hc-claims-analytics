package com.zhi.claims_analytics.controller;




import com.zhi.claims_analytics.dto.*;
import com.zhi.claims_analytics.model.Claim;
import com.zhi.claims_analytics.repository.ClaimRepository;
import com.zhi.claims_analytics.service.ClaimsService;
import com.zhi.claims_analytics.service.ClaimsUploadService;
import org.springframework.web.bind.annotation.*;

import com.zhi.claims_analytics.service.ClaimsAnalyticsService;
import com.zhi.claims_analytics.service.ClaimsUploadService;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.math.BigDecimal;


import java.io.InputStreamReader;
import java.io.Reader;


//lets us use POST
import org.springframework.web.bind.annotation.PostMapping;
//lets us use REQUEST
import org.springframework.web.bind.annotation.RequestParam;
//lets us upload files
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    public final ClaimsAnalyticsService claimsAnalyticsService;
    private final ClaimsService claimsService;
    private final ClaimsUploadService claimsUploadService;

    //Injected Instances of services & repo
    public ClaimController(ClaimsService claimsService,
                           ClaimsAnalyticsService claimsAnalyticsService,
                           ClaimsUploadService claimsUploadService
    ){
        this.claimsAnalyticsService = claimsAnalyticsService;
        this.claimsService = claimsService;
        this.claimsUploadService = claimsUploadService;

    }

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimsService.getAllClaims();
    }

    @GetMapping("/analytics/by-state")
    public List<ClaimsByStateDTO> getClaimsByState() {
        return claimsAnalyticsService.getClaimsByState();
    }

    @PostMapping("/upload")
    public UploadResponseDTO uploadClaimsCsv(@RequestParam("file") MultipartFile file) {
        //return "File received: " + file.getOriginalFilename();
        return claimsUploadService.uploadClaims(file);
    }

    @GetMapping("/analytics/by-status")
    public List<ClaimsByStatusDTO> getClaimsByStatus(){
        return claimsAnalyticsService.getClaimsByStatus();
    }

    @GetMapping("/analytics/total-by-state")
    public List<ClaimsTotalByStateDTO> getTotalClaimAmountByState(){
        return claimsAnalyticsService.getTotalClaimAmountByState();
    }

    @GetMapping("/analytics/top-providers")
    public List<TopProviderDTO> findTopProviders(){
        return claimsAnalyticsService.findTopProviders();
    }


}