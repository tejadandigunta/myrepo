package com.track.triaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.snc.selenium.core.SNCTest;
import com.snc.selenium.runner.GlideUiRunner;

@RunWith(GlideUiRunner.class)
public class TrackResultsIT extends SNCTest {
	private final String TRACK_RESULTS = "https://buildtools1.service-now.com/nav_to.do?uri=%2FgenerateTestResultsForTrack.do";
	private final String TRACK = "track";
	private final String PARENT_TRACK = "parent_track";
	private final String TEST_OBJECTS = "Test Objects";
	private final String TR = "tr";
	private final String CLASS = "class";
	private final String PROJECT_OWNER = "project_owner";
	private final String PROJECT_TITLE = "project_title";
	private final String ODD = "odd";
	private final String EVEN = "even";
	private final String HREF = "href";
	private final String EQUALS = "=";
	private final String PROPERTIES_FILE = "Triaging.properties";

	private final String LATEST_BUILD_VERSION = ".list2_body tr:nth-child(1) td:nth-child(3) a";
	private final String ID_TRACK_SELECT = "select2-chosen-1";
	private final String CSS_TRACK_INPUT = ".select2-search input";
	private final String ID_BUILD_LOOKUP = "lookup.select_build2";
	private final String ID_GENERATE_TEST_RESULTS = "ok_button";
	private final String CSS_FAILURE_TABLE = "table.fixed";
	private final String CSS_FAILURE_CELL = "td.fixed_cell a";
	private final String ID_USER_NAME = "user_name";
	private final String ID_PASSWORD = "user_password";
	private final String ID_LOGIN = "sysverb_login";
	private final String ID_MAIN_FRAME = "gsft_main";
	private final String ID_DYNAMIC_FRAME = "DynamicIFrame";
	private final String CSS_GROUP = "span.list_group";
	private final String XPATH_PARENT_TRACK = "//span[contains(text(),'%s')]";
	private final String XPATH_TEST_OBJECT = "//div[text()='Test Object:']/a[1]";
	private final String XPATH_SEARCH_GROUP = "//input[@aria-label='Search column: group']";
	private final String XPATH_SEARCH_BUILD = "//input[@aria-label='Search column: glide version']";

	private final int WAIT = 60;

	private String fMainWindowHandle;
	private String USER_NAME = "user_name";
	private String PWD = "pwd";
	private String QE = "QE";
	private String BUILD = "build";
	private String testObject;
	private HashMap<String, List<ProjectDetails>> testMatrix = new LinkedHashMap<>();
	private ArrayList<TestFailure> testFailures = new ArrayList<>();
	
	Properties prop;

	@Test
	public void testTriage() {
		loadProperties();
		openTrackResults();
		readTestFailures();
		processTestFailures();
		writeToExcel();
	}
	
	public void loadProperties() {
		prop = new Properties(PROPERTIES_FILE);
	}
	
	public void openTrackResults() {
		getDriver().get(TRACK_RESULTS);
		login();
		getDriver().switchTo().frame(ID_MAIN_FRAME);
		item.findElement(By.id(ID_TRACK_SELECT)).click();
		item.findElement(By.cssSelector(CSS_TRACK_INPUT)).sendKeys(prop.getProperty(TRACK));
		item.findElement(By.cssSelector(CSS_TRACK_INPUT)).sendKeys(Keys.ENTER);
		item.findElement(By.id(ID_BUILD_LOOKUP)).click();
		switchToWindowByTitle(TEST_OBJECTS);
		item.findElement(By.xpath(XPATH_SEARCH_GROUP)).sendKeys(EQUALS + prop.getProperty(TRACK) + Keys.ENTER);
		timers.waitUntilDOMReady(WAIT);
		if (prop.hasProperty(BUILD)) {
			item.findElement(By.xpath(XPATH_SEARCH_BUILD)).sendKeys(EQUALS + prop.getProperty(BUILD) + Keys.ENTER);
			timers.waitUntilDOMReady(WAIT);
		}
		item.findElement(By.cssSelector(LATEST_BUILD_VERSION)).click();
		switchToMainWindow();
		item.findElement(By.id(ID_GENERATE_TEST_RESULTS)).click();
		timers.waitUntilDOMReady(2*WAIT);
	}
	
	public void login() {
		timers.waitUntilDOMReady(2*WAIT);
		getDriver().switchTo().frame(ID_MAIN_FRAME);
		getDriver().findElement(By.id(ID_USER_NAME)).sendKeys(prop.getProperty(USER_NAME));
		item.findElement(By.id(ID_PASSWORD)).sendKeys(prop.getProperty(PWD));
		item.findElement(By.id(ID_LOGIN)).click();
		getDriver().switchTo().defaultContent();
		timers.waitUntilDOMReady(WAIT);
	}

	public void readTestFailures() {
		getDriver().switchTo().frame(ID_DYNAMIC_FRAME);
		testObject = item.findElement(By.xpath(XPATH_TEST_OBJECT)).getText();
		testObject = testObject.substring(testObject.indexOf("(")+1, testObject.length()-1);
		List<WebElement> failureTable = item.findElements(By.cssSelector(CSS_FAILURE_TABLE));
		ProjectDetails pro = null;
		String projectOwner = null;
		for(WebElement failure:failureTable) {
			List<WebElement> tableRows = failure.findElements(By.tagName(TR));
			
			for(WebElement row:tableRows) {
				switch(row.getAttribute(CLASS)) {
				case PROJECT_OWNER:
					if(pro!=null) {
						testMatrix.get(projectOwner).add(pro);
						pro=null;
					}
					projectOwner = row.getText();
					testMatrix.put(projectOwner,new ArrayList<ProjectDetails>());
					break;
				case PROJECT_TITLE:
					if(pro!=null) {
						testMatrix.get(projectOwner).add(pro);
						pro=null;
					}
					pro = new ProjectDetails();
					pro.setProjectName(row.getText());
					break;
				case ODD:
				case EVEN:
					WebElement elm = row.findElement(By.cssSelector(CSS_FAILURE_CELL));
					pro.addTestFailure(new TestFailure(elm.getText(),elm.getAttribute(HREF)));
					break;
				}
			}
		}
		testMatrix.get(projectOwner).add(pro);
	}
	
	//Loop through each owner and each project and get all test failures
	public void getTestFailures() {
		for(String owner:testMatrix.keySet()) {
			for(ProjectDetails p:testMatrix.get(owner)) {
				testFailures.addAll(p.getTestFailures());
			}
		}
	}
	
	public void processTestFailures() {
		getTestFailures();
		for(TestFailure test:testFailures) {
			try {
				getDriver().get(test.getUrl());
				timers.waitUntilDOMReady(2*WAIT);
			}
			catch(Exception e) {
				//Rule 1 - Mark as failure in case if test failure link has not loaded
				test.setFlapperCount(0);
				continue;
			}
			List<WebElement> groups = item.findElements(By.cssSelector(CSS_GROUP));
			//Rule 2 - mark as failure if number of tracks on which test is failing is less than MIN_FLAPPER_COUNT in TestFailure.java
			test.setFlapperCount(groups.size());
			//Rule 3 - Mark as failure if test has maximum number of failures on this track
			if(trackHasMaxFailures(groups))
				test.setFailure();
			//Rule 4 - Mark this flag as true if test is failing also on parent track
			test.setParentFailure(item.isElementPresent(String.format(XPATH_PARENT_TRACK, prop.getProperty(PARENT_TRACK)), WAIT/20));
		}
	}
	
	public boolean trackHasMaxFailures(List<WebElement> groups) {
		int failureCountOnThisTrack=0;
		int maxFailureCountOnOtherTrack=0;
		for (WebElement g:groups) {
			String groupName = g.getText();
			if (groupName.contains(prop.getProperty(TRACK))) {
				failureCountOnThisTrack = getCount(groupName);
			}
			else {
				int failureCount = getCount(groupName);
				maxFailureCountOnOtherTrack = (failureCount > maxFailureCountOnOtherTrack) ? failureCount : maxFailureCountOnOtherTrack;
			}
		}
		return (failureCountOnThisTrack >= maxFailureCountOnOtherTrack) ? true : false;
	}
	
	private int getCount(String groupName) {
		return Integer.parseInt(groupName.substring(groupName.lastIndexOf('(')+1, groupName.lastIndexOf(')')));
	}
	
	public void writeToExcel() {
		ExcelUtils.createExcelSheet(testObject);
		for(String owner:testMatrix.keySet()) {
			ExcelUtils.writeProjectOwner(owner);
			for(ProjectDetails p:testMatrix.get(owner)) {
				ExcelUtils.writeProjectName(p.getProjectName());
				ExcelUtils.writeTestFailures(p.getTestFailures(),prop.getProperty(QE));
			}
		}
		ExcelUtils.writeExcel(prop.getProperty(USER_NAME));
	}

	public void switchToWindowByTitle(String title) {
		fMainWindowHandle = getDriver().getWindowHandle();
		Set<String> windowHandles = getDriver().getWindowHandles();
		for (String handle : windowHandles) {
			getDriver().switchTo().window(handle);
			timers.waitUntilDOMReady(60);
			if (getDriver().getTitle().contains(TEST_OBJECTS)) {
				break;
			}
		}
	}

	public void switchToMainWindow() {
		getDriver().switchTo().window(fMainWindowHandle);
	}
}