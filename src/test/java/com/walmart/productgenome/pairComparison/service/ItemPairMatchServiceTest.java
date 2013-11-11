package com.walmart.productgenome.pairComparison.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.walmart.productgenome.pairComparison.audit.AuditDataCollector;
import com.walmart.productgenome.pairComparison.audit.ItemPairAuditDataCollector;
import com.walmart.productgenome.pairComparison.audit.RuleAuditEntity;
import com.walmart.productgenome.pairComparison.model.Constants;

/**
 * Test methods for {@link com.walmart.productgenome.pairComparison.service.ItemPairMatchService}
 * @author sprasa4
 *
 */
public class ItemPairMatchServiceTest {
	
	/**
	 * Loads sample rule and data file and returns audit information to check if the end-to-end
	 * matching pipeline is working fine.
	 */
	@Test
	public void testWSECNETBookProductMatchRun()
	{
		String rootPath = System.getProperty("user.dir");
		String ruleFilePath = Constants.WSE_CNET_BOOK_RULE_FILE_PATH;
		String bookDataFileLoc = rootPath + "/src/main/resources/data/wse_cnet_book_data.txt" ;
		runTest("WSE-CNET Book Mismatched Data", ruleFilePath, bookDataFileLoc);
	}

	/**
	 * Runs product matching using Walmart Search Extract and Bowker matching rules on a sample
	 * matched datatset here :
	 * http://pg-dev02.sv.walmartlabs.com:9998/data%3D2013-06-08_run%3D2013-06-30-run22/
	 */
	@Test
	public void testWSEBowkerMatchedDataTrialRun()
	{
		String rootPath = System.getProperty("user.dir");
		String ruleFilePath = Constants.WSE_BOWKER_RULE_FILE_PATH;
		String bookDataFileLoc = rootPath + "/src/main/resources/data/wse_bowker_matched.txt" ;
		runTest("WSE-Bowker Matched Data",ruleFilePath, bookDataFileLoc);
	}

	/**
	 * Runs product matching using Walmart Search Extract and Bowker matching rules on a sample
	 * matched datatset here :
	 * http://pg-dev02.sv.walmartlabs.com:9998/data%3D2013-06-08_run%3D2013-06-30-run22/
	 */
	@Test
	public void testWSEBowkerMismatchedDataTrialRun()
	{
		String rootPath = System.getProperty("user.dir");
		String ruleFilePath = Constants.WSE_BOWKER_RULE_FILE_PATH;
		String bookDataFileLoc = rootPath + "/src/main/resources/data/wse_bowker_mismatched.txt" ;
		runTest("WSE-Bowker Mismatched Data", ruleFilePath, bookDataFileLoc);
	}

	/**
	 * Runs product matching using Walmart Search Extract and Bowker matching rules on a sample
	 * matched datatset here :
	 * http://pg-dev02.sv.walmartlabs.com:9998/data%3D2013-06-08_run%3D2013-06-30-run22/
	 */
	@Test
	public void testWSECNETMatchedDataTrialRun()
	{
		String rootPath = System.getProperty("user.dir");
		String ruleFilePath = Constants.WSE_CNET_RULE_FILE_PATH;
		String bookDataFileLoc = rootPath + "/src/main/resources/data/wse_cnet_matched.txt" ;
		runTest("WSE-CNET Matched Data",ruleFilePath, bookDataFileLoc);
	}

	/**
	 * Runs product matching using Walmart Search Extract and Bowker matching rules on a sample
	 * matched datatset here :
	 * http://pg-dev02.sv.walmartlabs.com:9998/data%3D2013-06-08_run%3D2013-06-30-run22/
	 */
	@Ignore
	public void testWSECNETMismatchedDataTrialRun()
	{
		String rootPath = System.getProperty("user.dir");
		String ruleFilePath = Constants.WSE_CNET_RULE_FILE_PATH;
		String bookDataFileLoc = rootPath + "/src/main/resources/data/wse_cnet_mismatched.txt" ;
		runTest("WSE-CNET Mismatched Data", ruleFilePath, bookDataFileLoc);
	}

	@Test
	public void testTextBasedAdHocProductMatch()
	{
		// Sample itempair in the accepted data format
		String dataText = 
			"ID|#S5903977|#10603646|#" + "\n" +
			"Source|#CNET|#WALMART_SEARCH_EXTRACT|#" + "\n" +
			"Title|#Midland WR 11 - Weather alert radio|#Midland All-Hazard Weather Alert Table Radio|#" + "\n" +
			"pd_title|#[Midland WR 11 - Weather alert radio]|#[Midland All-Hazard Weather Alert Table Radio]|#" + "\n" +
			"req_upc_10|#[4601474113]|#[4601474113, 0460147411]|#" + "\n\n";
		
		// Sample valid rule
		String ruleText = 
			"CREATE DEFAULT_RULESET_ATTRIBUTES AS" + "\n" +
				"COMPARER = 						EXACT;" + "\n" +
				"SOURCE_TOKENIZER = 				STANDARD_ANALYZER;" + "\n" +
				"TARGET_TOKENIZER = 				STANDARD_ANALYZER;" + "\n" +
				"MISSING_ATTRIBUTE_ALLOWED = 		FALSE;" + "\n" +
				"SCORE = 							1.0;" + "\n" +
			"END" + "\n" +

			"CREATE RULE upc-title-rule AS" + "\n" +
			"	MATCH  [req_upc_10]  IN [req_upc_10, pd_title]" + "\n" +
			"	USING  SOURCE_TOKENIZER=NONE;" + "\n" + 			
			"END" + "\n" +

			"CREATE RULESET book_matching_rules AS" + "\n" + 
			"	INCLUDE RULE upc-title-rule ;" + "\n" +
			"END" + "\n";
		
		AuditDataCollector collector = ItemPairDataMatchService.runProductMatchJob(ruleText, dataText);
		assertNotNull(collector);
		System.out.println("Statistics : " + collector.getMatchSummaryStatistics());
	}
	
	// Utility method that invokes the actual product match run.
	private static void runTest(String testName, String ruleFilePath, String dataFilePath)
	{
		File ruleFile = new File(ruleFilePath);
		File dataFile = new File(dataFilePath);
		
		AuditDataCollector collector = ItemPairDataMatchService.runProductMatchJob(ruleFile, dataFile);
		
		// For a successful run, audit information would be available and is an indicator that the
		// rules were triggered properly.
		assertNotNull(collector);
		
		List<ItemPairAuditDataCollector> itemPairAuditList = collector.getItemPairAuditExtract();
		assertNotNull(itemPairAuditList);
		assertFalse(itemPairAuditList.isEmpty());
		
		for(ItemPairAuditDataCollector itemPairAudit : itemPairAuditList){
			assertNotNull(itemPairAudit);
			
			List<RuleAuditEntity> ruleAuditValues = itemPairAudit.getRuleAuditValues();
			assertNotNull(ruleAuditValues);
			assertFalse(ruleAuditValues.isEmpty());			
		}
		
		System.out.println("Test " + testName + " statistics : " + collector.getMatchSummaryStatistics());
	}
	
}
