package com.track.triaging.test;

import java.io.File;

import org.junit.Test;
import com.track.triaging.ExcelUtils;
import com.track.triaging.TestFailure;

public class TestClass {
	
//	@Test
	public void demoTest() {
		final String NAME = "16.uxidc.0.100";
		ExcelUtils.createExcelSheet(NAME);
		ExcelUtils.writeProjectOwner("Chris sheddy");
		ExcelUtils.writeProjectName("forms and lists");
//		TestFailure test1 = new TestFailure("Test1 failure in box", "https://www.google.com/");
//		test1.setFlapperCount(11);
		String url = "https://buildtools1.service-now.com/sdlc_test_result_list.do?sysparm_query=test=245586f74fb823443c1e7fe24210c703^execution.object.is_nightly=true^sys_created_onONLast%2030%20days%40javascript%3Ags.daysAgoStart(30)%40javascript%3Ags.daysAgoEnd(0)^result=failure^ORresult=NULL^GROUPBYexecution.object.group^short_error_messageSTARTSWITHexpected%3A%3C0%3E%20but%20was%3A";
		TestFailure test2 = new TestFailure("Test 2 failure ridiculous unknowing analysisIT", url);
		test2.setFlapperCount(4);
		
		
//		ExcelUtils.writeTestFailure(test1, "Teja");
		ExcelUtils.writeTestFailure(test2, "Teja");
		ExcelUtils.writeExcel("teja.dandigunta");
		System.out.println("completed");
	}
	
	@Test
	public void myDemo() {
	        File f = new File("src/test/ListExportDownloads");
//	        f.mkdir();
	        f.delete();
	         System.out.println(f.getAbsolutePath());
	}
}
