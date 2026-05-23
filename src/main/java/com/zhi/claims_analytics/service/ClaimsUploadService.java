package com.zhi.claims_analytics.service;

import com.zhi.claims_analytics.dto.UploadResponseDTO;
import com.zhi.claims_analytics.model.Claim;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhi.claims_analytics.repository.ClaimRepository;



import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClaimsUploadService {
    private final ClaimRepository claimRepository;

    public ClaimsUploadService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Transactional
    public UploadResponseDTO uploadClaims(MultipartFile file) {
        //return "File received: " + file.getOriginalFilename();
        try (
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVParser csvParser = new CSVParser(
                        reader,
                        CSVFormat.DEFAULT.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .setTrim(true)
                                .build()
                )
        ) {
            List<Claim> claims = new ArrayList<>();

            System.out.println(csvParser.getHeaderMap().keySet());
            validateRequiredHeaders(csvParser);
            for (CSVRecord record : csvParser) {

//                String patientId = record.get("patient_id");
//                String providerId = record.get("provider_id");
//                String state = record.get("state");

                //call helper method
                validateRequiredFields(record);

                Claim claim = new Claim();

                claim.setPatientId(record.get("patient_id"));
                claim.setProviderId(record.get("provider_id"));
                claim.setState(record.get("state"));
                claim.setClaimAmount(new BigDecimal(record.get("claim_amount")));
                claim.setStatus(record.get("status"));

                claims.add(claim);
            }
            claimRepository.saveAll(claims);
            //return  "Uploaded " + claims.size() + " claims successfully";
            return new UploadResponseDTO(
                    true,
                    claims.size(),
                    "Claims uploaded successfully"
            );
        } catch (Exception e) {
            //if upload fails
            return new UploadResponseDTO(
                    false,
                    0,
                    "Upload Failed: " + e.getMessage()
            );
        }
    }


    //Sanity checks if columns of input data are valid
    private void validateRequiredHeaders(CSVParser csvParser){
        String[] requiredHeaders = {
                "patient_id",
                "provider_id",
                "claim_amount",
                "state",
        };

        List<String> missingHeaders = new ArrayList<>();

        for (String header : requiredHeaders) {
            if (!csvParser.getHeaderMap().containsKey(header)){
                missingHeaders.add(header);
            }
        }

        if (!missingHeaders.isEmpty()){
            throw new IllegalArgumentException("Missing required CSV columns: " + missingHeaders);
        }
    }


    // Sanity checks if input fields fit properly into our table
    private void validateRequiredFields(CSVRecord record){

        String[] requiredFields = {
                "patient_id",
                "provider_id",
                "state",
                "claim_amount"
        };

        List<String> missingFields = new ArrayList<>();

        for (String field : requiredFields) {
            if (record.get(field).isBlank()){
                missingFields.add(field);
            }
        }

        if (!missingFields.isEmpty()){
            throw new IllegalArgumentException("Missing required data entry fields: " + missingFields);
        }
    }
}
