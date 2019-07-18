package com.track.triaging;

public class TestFailure {
	private String name;
	private String url;
	private String status;
	private int flapperCount;
	private boolean failingOnParent;
	private final int MIN_FLAPPER_COUNT = 7;
	
	private final String LAST_30_DAYS = "Last%2030%20days";
	private final String LAST_7_DAYS = "Last%207%20days";
	private final String DAYS_AGO_START = "gs.daysAgoStart(30)";
	private final String BEGINNING = "gs.beginningOfLast7Days()";
	private final String DAYS_AGO_END = "gs.daysAgoEnd(0)";
	private final String ENDING = "gs.endOfLast7Days()";
	
	
	private final String FLAPPER = "Flapper";
	private final String FAILURE = "Failure";
	
	public TestFailure(String name, String url) {
		this.name = name;
		this.url = replaceTilde(url);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	private void setStatus() {
		if(flapperCount > MIN_FLAPPER_COUNT) {
			this.status = FLAPPER;
		}
		else {
			this.status = FAILURE;
		}
	}
	
	public void setFailure() {
		this.status = FAILURE;
	}
	
	public void setFlapperCount(int count) {
		this.flapperCount = count;
		setStatus();
	}
	
	public void setParentFailure(boolean failure) {
		failingOnParent = failure;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getStatus() {
		return status;
	}
	
	public int getFlapperCount() {
		return flapperCount;
	}
	
	public boolean getParentFailure() {
		return failingOnParent;
	}
	
	public String toString() {
		String record = this.getName() + " " + this.getUrl() + " " + this.getStatus() + " " + this.getFlapperCount();
		if(this.getStatus().equals(FLAPPER))
			record += this.getParentFailure();
		return record;
	}
	
	private String replaceTilde(String str) {
		String[] splitters = {"execution.object", "sys_created_on", "result=failure", "ORresult=NULL", "GROUPBY", "short_error_message"};
		str = str.replaceAll(LAST_30_DAYS, LAST_7_DAYS).replaceAll(DAYS_AGO_START, BEGINNING).replaceAll(DAYS_AGO_END, ENDING);
		String res = "";
		for(String s:splitters) {
			res = res + str.substring(0, str.indexOf(s) - 1) + "%5E";
			str = str.substring(str.indexOf(s));
		}
		return res + str;
	}
}