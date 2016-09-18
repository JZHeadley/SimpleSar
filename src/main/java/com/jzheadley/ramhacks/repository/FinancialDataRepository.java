package com.jzheadley.ramhacks.repository;

import com.jzheadley.ramhacks.domain.FinancialData;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the FinancialData entity.
 */
public interface FinancialDataRepository extends JpaRepository<FinancialData,Long> {

}
