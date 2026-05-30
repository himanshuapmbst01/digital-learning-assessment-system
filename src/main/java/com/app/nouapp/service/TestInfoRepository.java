package com.app.nouapp.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.nouapp.model.TestInfo;

public interface TestInfoRepository extends JpaRepository<TestInfo, Long>{

	boolean existsByTestname(String testname);

}
