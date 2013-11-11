package com.walmart.productgenome.pairComparison.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.walmart.productgenome.pairComparison.audit.AuditDataCollector;
import com.walmart.productgenome.pairComparison.audit.ItemPairAuditDataCollector;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntityPair;
import com.walmart.productgenome.pairComparison.parser.ParseException;

/**
 * Service class for matching items using a set of rules defined in a domain specific rule language. 
 * The item pair data file and rule file are input to this service class and it returns the
 * summary as well audit results of matching for debugging and analysis.
 * 
 * @author sprasa4
 *
 */
public class ItemPairDataMatchService {
	
	/**
	 * Run product matching job for the specified rule file path and data file path. This serves
	 * the file-upload based matching option in Product Matching Web Tool.
	 * 
	 * @param ruleFile 		File containing matching rules defined in domain specific language.
	 * @param dataFile		File containing itempair data that has to be matched.
	 * @return Detailed audit information about the whole product match trial.
	 */
	public static AuditDataCollector runProductMatchJob(File ruleFile, File dataFile)
	{
		long startTime = System.currentTimeMillis();
		
		List<MatchEntityPair> itemPairs = null; 
		try {
			itemPairs = ItemPairDataFileReader.getDataFileItemPairs(dataFile);
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to parse data file : " + dataFile.getAbsolutePath() , e);
		}
		
		ItemPairDataMatcher matcher = null; 
		try {
			matcher = new ItemPairDataMatcher(ruleFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Rule file " + ruleFile.getAbsolutePath() + " not found !!", e);
		} catch (ParseException e) {
			throw new RuntimeException("Failed to parse the rule file : " + ruleFile.getAbsolutePath(), e);
		}
		
		AuditDataCollector auditDataCollector = new AuditDataCollector();
		for(MatchEntityPair itemPair : itemPairs) {
			ItemPairAuditDataCollector auditData = new ItemPairAuditDataCollector(itemPair);
			matcher.matchItemPair(itemPair, auditData);			
			auditDataCollector.addItemPairAuditInfo(auditData);
		}
		auditDataCollector.setRulesetUsed(matcher.getRuleset().getRulesetName());
		
		// TODO : Hack setting the state indirectly here. How else can I trigger this ?
		auditDataCollector.generateAuditStatistics();

		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to match " + itemPairs.size() + " itempairs : " + 
				            (endTime - startTime)/1000 + " seconds.");
		return auditDataCollector;
	}

	/**
	 * Run product matching for the specified rules and item pair data. This serves the ad-hoc
	 * product matching option in the Product Matching Web Tool.
	 * 
	 * @param ruleText	Product matching rules as plain text.
	 * @param dataText	Item Pair data to be macthed as plain text.
	 * @return	Detailed audit information about the whole product match trial.
	 */
	public static AuditDataCollector runProductMatchJob(String ruleText, String dataText)
	{
		if(ruleText == null || ruleText.isEmpty()) 
			throw new IllegalArgumentException("Please submit non-empty rules data !!");
		if(dataText == null || dataText.isEmpty()) 
			throw new IllegalArgumentException("Please submit non-empty itempair data !!");
		
		// Stage the plain text data into temporary files
		File ruleFile = stageTextDataIntoFile(ruleText, "rule");
		File dataFile = stageTextDataIntoFile(dataText, "data");
		
		return runProductMatchJob(ruleFile, dataFile);
	}
	
	// Stage the plain text data temporarily into a file
	private static File stageTextDataIntoFile(String textToStage, String fileNamePrefix)
	{
		String tempFileName = fileNamePrefix + "Temp" + ".txt";
		File tempFile = new File("/tmp/" + tempFileName);
		if(tempFile.exists()) {
			tempFile.delete();
			tempFile = new File("/tmp/" + tempFileName);
		}
		
		try {
			FileWriter fw = new FileWriter(tempFile);
			BufferedWriter bw = new BufferedWriter(fw);			
			bw.write(textToStage);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to stage the plaintext into temp file " + tempFile.getAbsolutePath());
		}
		
		return tempFile;
	}
}
