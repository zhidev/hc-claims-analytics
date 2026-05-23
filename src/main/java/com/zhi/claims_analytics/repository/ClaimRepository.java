package com.zhi.claims_analytics.repository;

import com.zhi.claims_analytics.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface ClaimRepository extends JpaRepository<Claim, Long>  {
    @Query(value = """
    SELECT state, COUNT(*)
    FROM claims
    GROUP BY state
    """, nativeQuery = true)
    List<Object[]> countClaimsByState();

    @Query(value = """
            SELECT status, COUNT(*) AS claim_count
            FROM claims
            GROUP BY status
            ORDER BY claim_count DESC
            """, nativeQuery = true)
    List<Object[]> countClaimsByStatus();

    @Query(value = """
            SELECT state, SUM(claim_amount) AS total_claim_count
            FROM claims
            GROUP BY state
            ORDER BY total_claim_count
            """, nativeQuery = true)
    List<Object[]> sumClaimAmountByStatus();

    @Query(value = """
        SELECT provider_id, COUNT(*) AS claim_count
        FROM claims
        GROUP BY provider_id
        ORDER BY claim_count DESC
        LIMIT 10 
    """, nativeQuery = true)
    List<Object[]> findTopProviders();
}
