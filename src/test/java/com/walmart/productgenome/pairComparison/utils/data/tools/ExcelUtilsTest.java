package com.walmart.productgenome.pairComparison.utils.data.tools;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test class for 
 * @see {com.walmart.productgenome.pairComparison.utils.data.tools.ExcelUtils}
 * @author sprasa4
 *
 */
public class ExcelUtilsTest {

	@Test
	public void testWriteToExcelFile()
	{
		ExcelUtils writer = new ExcelUtils("test", "/Users/sprasa4/Desktop");
		writer.writeRowToExcelFile(Lists.newArrayList("id", "name"));
		writer.writeRowToExcelFile(Lists.newArrayList("1", "Ipad"));
		writer.writeRowToExcelFile(Lists.newArrayList("2", "Iphone"));
		writer.writeRowToExcelFile(Lists.newArrayList("3", "Ipod"));
		writer.saveExcelFile();
	}
}
