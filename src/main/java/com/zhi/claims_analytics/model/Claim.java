package com.zhi.claims_analytics.model;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    //so the following at a high level gets JPA to auto increment off the primary key which is
    //claim_id, and our example is 1000, 1001, 1002, etc. so it adds the next number off our PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long claimId;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "diagnosis_code")
    private String diagnosisCode;

    @Column(name = "procedure_code")
    private String procedureCode;

    @Column(name = "claim_amount")
    private BigDecimal claimAmount;

    private String state;

    @Column(name = "claim_date")
    private LocalDate claimDate;

    private String status;

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getDiagnosisCode() { return diagnosisCode; }
    public void setDiagnosisCode(String diagnosisCode) { this.diagnosisCode = diagnosisCode; }

    public String getProcedureCode() { return procedureCode; }
    public void setProcedureCode(String procedureCode) { this.procedureCode = procedureCode; }

    public BigDecimal getClaimAmount() { return claimAmount; }
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public LocalDate getClaimDate() { return claimDate; }
    public void setClaimDate(LocalDate claimDate) { this.claimDate = claimDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}