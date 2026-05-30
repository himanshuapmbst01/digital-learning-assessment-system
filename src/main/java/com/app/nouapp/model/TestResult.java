package com.app.nouapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "testresult")
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long resid;

    @Column(nullable = false, length = 20)
    private String enrollmentno;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, length = 13)
    private String contactno;

    @Column(length = 100)
    private String collegename;

    @Column(length = 50)
    private String course;

    @Column(length = 100)
    private String branch;

    @Column(length = 50)
    private String year;

    @Column(nullable = false, length = 10)
    private String status;

    @Column(nullable = false)
    private int totalmarks;

    @Column(nullable = false)
    private int getmarks;
    
    @Column(nullable = false)
    private long testId;

    @Column(nullable = false, length = 100)
    private String testname;

    @Column(nullable = false)
    private LocalDateTime submittedAt;   // Added new column for submission date-time

    public long getResid() {
        return resid;
    }

    public void setResid(long resid) {
        this.resid = resid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getCollegename() {
        return collegename;
    }

    public void setCollegename(String collegename) {
        this.collegename = collegename;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalmarks() {
        return totalmarks;
    }

    public void setTotalmarks(int totalmarks) {
        this.totalmarks = totalmarks;
    }

    public int getGetmarks() {
        return getmarks;
    }

    public void setGetmarks(int getmarks) {
        this.getmarks = getmarks;
    }

    public String getTestname() {
        return testname;
    }

    public long getTestId() {
		return testId;
	}

	public void setTestId(long testId) {
		this.testId = testId;
	}

	public void setTestname(String testname) {
        this.testname = testname;
    }

    public String getEnrollmentno() {
        return enrollmentno;
    }

    public void setEnrollmentno(String enrollmentno) {
        this.enrollmentno = enrollmentno;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
