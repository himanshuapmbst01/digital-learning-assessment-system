package com.app.nouapp.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.nouapp.model.QuestionBank;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, Integer>{

	@SuppressWarnings("unchecked")
	default List<QuestionBank> findQuestionbyYearAndCourseAndBranch(
	        String year, 
	        String course, 
	        String branch, 
	        int numberOfQuestion, 
	        EntityManager entityManager
	    ) {
	    String sqlquery = "SELECT * FROM questionbank WHERE year = :year AND course = :course AND branch = :branch ORDER BY RAND() LIMIT " + numberOfQuestion;
	    
	    Query query = entityManager.createNativeQuery(sqlquery, QuestionBank.class);
	    query.setParameter("year", year);
	    query.setParameter("course", course);
	    query.setParameter("branch", branch);

	    return query.getResultList();
	}

}
