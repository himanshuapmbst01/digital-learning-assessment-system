package com.app.nouapp.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.nouapp.model.TestResult;

public interface TestResultRepository extends JpaRepository<TestResult, Long>{

	List<TestResult> findResultByEnrollmentno(String enrollmentno);

	boolean existsByEnrollmentnoAndTestId(String enrollmentno, long TestId);

}
