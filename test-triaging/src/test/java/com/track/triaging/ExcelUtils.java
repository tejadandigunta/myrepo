package com.track.triaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	
	private static final int TOTAL_COLS = 4;
	private static Workbook wb;
	private static Sheet sh;
	private static int row=0;
	private static String workbookName;
	private static String workbookPath;
	private static FileOutputStream fos;
	
	private static enum FontType {HEADING, DETAIL, UNDERLINE};
	
	private static CellStyle owner, project, detail, flapper, failure, hyperLink;
	private static CreationHelper helper;
	
	private static final String FAILURE = "Failure";
	
	public static void createExcelSheet(String name) {
		ExcelUtils.workbookName = name + getUniqueTimeStamp() + ".xlsx";
		wb = new XSSFWorkbook();
		sh = wb.createSheet(name);
		helper = wb.getCreationHelper();
		createCellStyles();
	}
	
	public static void writeProjectOwner(String projectOwner) {
		Cell c = createRowAndCells(true).getCell(0);
		c.setCellValue(projectOwner);
		c.setCellStyle(owner);
	}
	
	public static void writeProjectName(String projectName) {
		Cell c = createRowAndCells(true).getCell(0);
		c.setCellValue(projectName);
		c.setCellStyle(project);
	}
	
	public static void writeTestFailure(TestFailure test, String QE) {
		Row currRow = createRowAndCells(false);
		currRow.getCell(0).setCellValue(test.getName());
		currRow.getCell(0).setCellStyle(hyperLink);
		
		Hyperlink hlink = helper.createHyperlink(HyperlinkType.FILE);
		hlink.setAddress(test.getUrl());
		currRow.getCell(0).setHyperlink(hlink);
		
		currRow.getCell(1).setCellValue(QE);
		currRow.getCell(1).setCellStyle(detail);
		
		String status = test.getStatus();
		currRow.getCell(2).setCellValue(status);
		if(status.equals(FAILURE))
			currRow.getCell(2).setCellStyle(failure);
		else
			currRow.getCell(2).setCellStyle(flapper);
		
		if(status.equals(FAILURE)) {
			currRow.getCell(3).setCellValue(test.getParentFailure());
			currRow.getCell(3).setCellStyle(detail);
		}
	}
	
	public static void writeTestFailures(List<TestFailure> tests, String QE) {
		for(TestFailure test: tests) {
			writeTestFailure(test,QE);
		}
	}
	
	private static Row createRowAndCells(boolean merge) {
		Row r = sh.createRow(row++);
		for(int col=0;col<TOTAL_COLS;col++) {
			r.createCell(col);
		}
		if(merge) {
			sh.addMergedRegion(new CellRangeAddress(row-1, row-1, 0, TOTAL_COLS-1));
		}
		return r;
	}
	
	public static void writeExcel(String userName) {
		for(int i=0; i< TOTAL_COLS; i++) {
			sh.autoSizeColumn(i);
		}
		setDesktopPath(userName);
		try {
			fos = new FileOutputStream(new File(workbookPath + workbookName));
			wb.write(fos);
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void createCellStyles() {
		owner = wb.createCellStyle();
		project = wb.createCellStyle();
		detail = wb.createCellStyle();
		failure = wb.createCellStyle();
		flapper = wb.createCellStyle();
		hyperLink = wb.createCellStyle();
		
		Font headingFont = getFont(FontType.HEADING);
		Font detailFont = getFont(FontType.DETAIL);
		Font underlineFont = getFont(FontType.UNDERLINE);
		
		owner.setFont(headingFont);
		owner.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		owner.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		project.setFont(headingFont);
		project.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		project.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		detail.setFont(detailFont);
		
		failure.setFont(detailFont);
		failure.setFillForegroundColor(IndexedColors.RED.getIndex());
		failure.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		flapper.setFont(detailFont);
		flapper.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		flapper.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		hyperLink.setFont(underlineFont);
	}
	
	private static Font getFont(FontType type) {
		Font f = wb.createFont();
		f.setFontName("Arial");
		switch(type) {
		case HEADING:
			f.setFontHeightInPoints((short) 14);
			f.setBold(true);
			break;
		case UNDERLINE:
			f.setUnderline(XSSFFont.U_SINGLE);
			f.setColor(IndexedColors.LIGHT_BLUE.getIndex());
		case DETAIL:
			f.setFontHeightInPoints((short) 12);
			break;
		}
		return f;
	}
	
	private static void setDesktopPath(String userName) {
		workbookPath = "/Users/" + userName + "/Desktop/";
	}
	
	private static String getUniqueTimeStamp() {
		String timeStamp = new SimpleDateFormat("_MMMdd_HHmmss").format(new Date());
		return timeStamp;
	}
}
