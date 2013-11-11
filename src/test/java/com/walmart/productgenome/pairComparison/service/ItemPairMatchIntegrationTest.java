package com.walmart.productgenome.pairComparison.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.Ignore;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.audit.ItemPairAuditDataCollector;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntityPair;
import com.walmart.productgenome.pairComparison.parser.ParseException;

public class ItemPairMatchIntegrationTest {

	private static String FILE_PATH_PREFIX = "/Users/sprasa4/Data/Work/resources/data/wse_cnet_mismatch/20120731/";
	public static void main(String[] args) throws IOException
	{
		System.out.println("Splitting new itempair file ..");
		splitLargeFile(FILE_PATH_PREFIX + "new_rule_extra_itempairs.txt", 100, FILE_PATH_PREFIX + "new_itempair_split/WSE_CNET_");

		/*
		System.out.println("Splitting new itempair file ..");
		splitLargeFile(FILE_PATH_PREFIX + "new_rule_extra_itempairs.txt", 100, FILE_PATH_PREFIX + "new_itempair_split/WSE_CNET_");
		
		System.out.println("Splitting old itempair file ..");
		splitLargeFile(FILE_PATH_PREFIX + "old_rule_extra_itempairs.txt", 100, FILE_PATH_PREFIX + "old_itempair_split/WSE_CNET_");
		*/
		//splitWSEStoresData();
	}

	private static void splitWSEStoresData() throws IOException
	{
		System.out.println("Splitting WSE stores data ..");
		splitLargeFile(FILE_PATH_PREFIX + "WALMART_SEARCH_EXTRACT_WALMART_STORES-pairs-all.txt", 100, FILE_PATH_PREFIX + "wse_stores_data/WSE_STORES_");		
	}
	
	private static void splitLargeFile(String largeFilePath, int splitThreshold, String writeFilePrefix) throws IOException
	{
		File dataFile = new File(largeFilePath);
		
		int writeFileCounter = 0;
		int numItemPairs = 0;
		
		// Large file reader helper variables
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));
		String currLine;
		
		// Small file writer helper variables
		String newWriteFile = writeFilePrefix + writeFileCounter;
		System.out.println("New write file : " + newWriteFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(newWriteFile)));
		
		while((currLine = reader.readLine()) != null) {
			// Signals the end of an itempair
			currLine = currLine.trim();
			if(currLine.isEmpty()) {
				writer.write(currLine);
				writer.newLine();
				++numItemPairs;
				
				// Save previous file and create a new file
				if(numItemPairs >= splitThreshold) {
					writer.close();
					numItemPairs=0;
					
					++writeFileCounter;
					newWriteFile = writeFilePrefix + writeFileCounter;
					System.out.println("New write file : " + newWriteFile);
					File writeFile = new File(newWriteFile);
					writer = new BufferedWriter(new FileWriter(writeFile));
				}
			}
			// Keep appending data to current small file
			else {
				if(currLine.contains("^A") || currLine.contains("\001")) {
					currLine = currLine.replaceAll("^A", ";#");
					currLine = currLine.replaceAll("\001", ";#");
				}

				writer.write(currLine);
				writer.newLine();
			}						
		}
		
		writer.close();
		reader.close();
	}
	
	/*
	public static void main(String[] args) throws FileNotFoundException, ParseException
	{
		testWSECNETAllItemPairs();
	}
	*/
	
	@Ignore
	public void testWSECNETAllItemPairs() throws FileNotFoundException, ParseException
	{
		System.out.println("Started matching of 673 files.");
		// Setup the rule file
		String ruleFilePath = Constants.WSE_CNET_RULE_FILE_PATH;
		File ruleFile = new File(ruleFilePath);
		ItemPairDataMatcher matcher = new ItemPairDataMatcher(ruleFile);

		int totalCount = 0;
		for(int i=0; i <=1347; i++) {
			String dataFilePath = "/tmp/wse_cnet_data/WSE_CNET_PAIRS_" + i;
			System.out.println("Matching file : " + dataFilePath);
			try {
				totalCount += testWSECNETMillionItemPairs(matcher, dataFilePath);
			} catch (Exception e) {
				System.err.println("Issue with datafile : " + dataFilePath);
				e.printStackTrace();
			}
		}
		System.out.println("Matched " + totalCount + " itempairs ..");
	}

	@Ignore
	public void testWSECNETAllItemPairsManualData() throws FileNotFoundException, ParseException
	{
		System.out.println("Started matching manual data ..");
		// Setup the rule file
		String ruleFilePath = Constants.WSE_CNET_RULE_FILE_PATH;
		File ruleFile = new File(ruleFilePath);
		ItemPairDataMatcher matcher = new ItemPairDataMatcher(ruleFile);

		String dataFilePath = "/Users/sprasa4/Data/Work/ProductMatcherService/src/main/resources/data/temp.txt";
		testWSECNETMillionItemPairs(matcher, dataFilePath);
		System.out.println("Finished matching manual data ..");
		
	}

	public static int testWSECNETMillionItemPairs(ItemPairDataMatcher matcher, String dataFilePath) throws FileNotFoundException, ParseException
	{
		File dataFile = new File(dataFilePath);		
		List<MatchEntityPair> itemPairs = ItemPairDataFileReader.getDataFileItemPairs(dataFile);
		
		int totalCount = 0;
		int matchedCount = 0;
		int mismatchedCount = 0;
		int failedCount = 0;
		
		//System.out.println("Starting matching ..");
		List<MatchEntityPair> failedItemPairs = Lists.newArrayList();
		for(MatchEntityPair itemPair : itemPairs) {
			try {
				boolean isMatched = matcher.matchItemPair(itemPair, new ItemPairAuditDataCollector(itemPair));
				if(isMatched) {
					++matchedCount;
				}
				else {
					++mismatchedCount;
				}
			}
			catch (Exception e) {
				System.err.println("Error occured during matching of " + itemPair.toString());
				++failedCount;
				failedItemPairs.add(itemPair);
			}
		}
		//System.out.println("Finished matching ..\n\n");
		
		
		//System.out.println("Total : " + itemPairs.size());
		//System.out.println("Matched : " + matchedCount);
		//System.out.println("Mismatched : " + mismatchedCount);
		
		if(failedCount > 0) {
			System.out.println("File : " + dataFilePath + ",  failed " + failedCount);
			//System.out.println("Failed itempairs : " + failedItemPairs.toString());			
		}
		
		return itemPairs.size();
	}

}
