package com.app.nouapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "testinfoes")
public class TestInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(length = 100)
	private String testname;
	
	@Column(length = 100, nullable = false)
	private String course;
	
	@Column(length = 100, nullable = false)
	private String branch;
	
	@Column(length = 50, nullable = false)
	private String year;
	
	@Column(nullable = false)
	private int numberofquestion;
	
	@Column(nullable = false)
	private LocalDateTime starttime;
	
	@Column(nullable = false)
	private int testDuration;
	
	@Column(nullable = false)
	private boolean active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTestname() {
		return testname;
	}

	public void setTestname(String testname) {
		this.testname = testname;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getNumberofquestion() {
		return numberofquestion;
	}

	public void setNumberofquestion(int numberofquestion) {
		this.numberofquestion = numberofquestion;
	}

	public LocalDateTime getStarttime() {
		return starttime;
	}

	public void setStarttime(LocalDateTime starttime) {
		this.starttime = starttime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
	public int getTestDuration() {
		return testDuration;
	}

	public void setTestDuration(int testDuration) {
		this.testDuration = testDuration;
	}

	public LocalDateTime endtime()
	{
		return starttime.plusHours(testDuration);
	}
}
