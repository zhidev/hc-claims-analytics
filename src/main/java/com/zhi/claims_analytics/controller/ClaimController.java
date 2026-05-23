package com.zhi.claims_analytics.controller;




import com.zhi.claims_analytics.model.Claim;
import com.zhi.claims_analytics.repository.ClaimRepository;
import org.springframework.web.bind.annotation.*;

import com.zhi.claims_analytics.dto.ClaimsByStateDTO;
import com.zhi.claims_analytics.service.ClaimsAnalyticsService;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

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

    public final ClaimRepository claimRepository;
    public final ClaimsAnalyticsService claimsAnalyticsService;

    public ClaimController(ClaimRepository claimRepository,
                           ClaimsAnalyticsService claimsAnalyticsService){
        this.claimsAnalyticsService = claimsAnalyticsService;
        this.claimRepository = claimRepository;
    }

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }
    @GetMapping("/analytics/by-state")
    public List<ClaimsByStateDTO> getClaimsByState() {
        return claimsAnalyticsService.getClaimsByState();
    }

    @PostMapping("/upload")
    public String uploadClaimsCsv(@RequestParam("file") MultipartFile file) {
        //return "File received: " + file.getOriginalFilename();
        try (
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVParser csvParser = new CSVParser(
                        reader,
                        CSVFormat.DEFAULT.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .build()
                )
        ) {
            List<Claim> claims = new ArrayList<>();
            for (CSVRecord record : csvParser) {

//                String patientId = record.get("patient_id");
//                String providerId = record.get("provider_id");
//                String state = record.get("state");

                Claim claim = new Claim();

                claim.setPatientId(record.get("patient_id"));
                claim.setProviderId(record.get("provider_id"));
                claim.setState(record.get("state"));

                claims.add(claim);
            }
            claimRepository.saveAll(claims);
            return  "Uploaded " + claims.size() + " claims successfully";

        } catch (Exception e) {
            return "Error parsing CSV:" + e;
        }
    }

}