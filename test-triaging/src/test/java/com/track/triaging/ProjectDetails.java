package com.track.triaging;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetails {
	private String projectName;
	List<TestFailure> tests = new ArrayList<>();
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void addTestFailure(TestFailure test) {
		tests.add(test);
	}
	
	public List<TestFailure> getTestFailures(){
		return tests;
	}
}