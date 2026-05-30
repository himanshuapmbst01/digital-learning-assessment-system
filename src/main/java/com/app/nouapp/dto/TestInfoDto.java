package com.app.nouapp.dto;

import java.time.LocalDateTime;

public class TestInfoDto {

	private long id;
	private String testname;
	private String course;
	private String branch;
	private String year;
	private int numberofquestion;
	private LocalDateTime starttime;
	private boolean active;
	private int testDuration;
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
	
}
