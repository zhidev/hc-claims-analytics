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
}
