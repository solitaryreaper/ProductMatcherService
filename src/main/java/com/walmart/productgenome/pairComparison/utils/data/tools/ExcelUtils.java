package com.walmart.productgenome.pairComparison.utils.data.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Publishes data to excel worksheet
 * 
 * @author sprasa4
 *
 */
public class ExcelUtils {
	// Path of the excel file 
	private String excelFilePath;
	
	private String workbookName;
	private HSSFWorkbook workbook;
	private HSSFSheet excelSheet;
	
	// Counter of the current row in excel file
	private int currRowNum = -1;
	
	public ExcelUtils(String workbookName, String excelFilePath)
	{
		this.workbookName = workbookName;
		this.excelFilePath = excelFilePath + "/" + workbookName + ".xls";
		this.workbook = new HSSFWorkbook();
		this.excelSheet = workbook.createSheet(workbookName);
	}
	
	/**
	 * Dump the current row to the excel file
	 * @param rowToWrite
	 */
	public void writeRowToExcelFile(List<String> rowToWrite)
	{
		currRowNum++;
		Row excelRow = excelSheet.createRow(currRowNum);
		
		int cellNum = -1;
		Cell excelCell = null;
		for(String cellValue : rowToWrite) {
			excelCell = excelRow.createCell(++cellNum);
			if(cellValue != null) {
				excelCell.setCellValue(cellValue);				
			}
			else {
				excelCell.setCellValue("NA");				
			}
		}		
	}
	
	/**
	 * Saves the excel file to the set file path with the chosen sheet name.
	 */
	public void saveExcelFile()
	{
		try {
		    FileOutputStream out = new FileOutputStream(new File(excelFilePath));
		    workbook.write(out);
		    out.close();
		    System.out.println("Excel sheet " + workbookName + " saved !!");
		     
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}		
	}
}
