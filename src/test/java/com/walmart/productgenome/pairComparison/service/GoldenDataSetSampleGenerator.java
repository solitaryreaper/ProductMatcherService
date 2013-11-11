package com.walmart.productgenome.pairComparison.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.utils.data.DataCleanupUtils;
import com.walmart.productgenome.pairComparison.utils.tokenizers.StandardAnalyzerTokenizer;

/**
 * Utility class that can be used to generate sample itempairs for golden dataset analysis.
 * 
 * The main idea is to find all the important categories in the data file and generate some
 * sample number of itempairs for each of those categories.
 * @author sprasa4
 *
 */
public class GoldenDataSetSampleGenerator {

	private static double DEFAULT_CATEGORY_PERCENT_OCCURENCE = 3;
	private static int DEFAULT_NUM_ITEMS_PER_CATEGORY = 25;
	
	private static String CATEGORY_ATTRIBUTE = "req_category";
	private static String BRAND_ATTRIBUTE    = "req_brand_name";
	
	private static Set<String> categoryStopwords = 
		Sets.newHashSet("total", "all", "unassigned", "special", "general", "generic", "other", "basic", "variety", "free");
	private static Set<String> brandStopwords = 
		Sets.newHashSet("online", "generic", "unbranded", "general", "none", "vendor");

	// A running list of all the itempairs that have already been included in the golden data set.
	// We don't want to include the same itempair twice across files
	private static Set<String> includedItemPairsSet = Sets.newHashSet();
	
	/**
	 * Use tool as follows :
	 * 
	 * java GoldenDataSetSampleGenerator "/Users/sprasa4/Data/Work/resources/data/wse_stores_data/WSE_STORES_0" /Users/sprasa4/Data/Work/resources/data/golden_data_wse_stores/WSE_STORES_GOLDEN_DS_" 3 25
	 */
	public static void main(String[] args)
	{
		if(args.length < 3 || args.length > 5) {
			System.out.println("Incorrect number of arguments.");
			System.out.println("java GoldenDataSetSampleGenerator <inputDataFilePath> <outputDataFilesPathPrefix> <categoryPercentOccurenceExpected> <numItemsPerCategoryRequired>");
			System.exit(1);
		}
		
		String inputDataFile = args[0];
		String dumpOutputFilePathPrefix = args[1];
		double categoryPercentOccurenceExpected = Double.parseDouble(args[2]);
		int numItemsPerCategoryRequired = Integer.parseInt(args[3]);
		
		System.out.println("Input File : " + args[0]);
		System.out.println("Output directory : " + args[1]);
		System.out.println("MIN percent required : " + args[2]);
		System.out.println("Number of Items Per Category : " + args[3]);

		try {
			generateGoldenDataSet(inputDataFile, dumpOutputFilePathPrefix, categoryPercentOccurenceExpected, numItemsPerCategoryRequired);
			System.out.println("Golden data generation completed successfully !!");
		} catch (IOException e) {
			System.err.println("Golden data set generation failed !!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates golden dataset for the input dataset.
	 * 
	 * @param numCategories
	 * @param numItemPairsPerCategory
	 * @param inputDataFile
	 * @param outputDataFilePrefix
	 * @throws IOException 
	 */
	private static void generateGoldenDataSet(
		String inputDataFile, String outputDataFilePrefix,
		double expectedCategoryPercent, int numItemPairsPerCategory) throws IOException
	{
		File dataFile = new File(inputDataFile);
		List<String> topCategoryKeywords = getFrequentCategories(expectedCategoryPercent, dataFile);
		System.out.println("Top auto-generated keywords : " + topCategoryKeywords.toString());
		
		dumpSampleItemPairsForEachCategory(topCategoryKeywords, dataFile, outputDataFilePrefix, DEFAULT_NUM_ITEMS_PER_CATEGORY);
	}
	
	/**
	 * Dumps a sample itempair file for each frequently occurring category
	 * @throws IOException 
	 */
	private static void dumpSampleItemPairsForEachCategory(List<String> topCategoryKeywords, 
			File dataFile, String dumpOutputFilePathPrefix, int numSamplePairsExpected) throws IOException
	{
		StandardAnalyzerTokenizer tokenizer = new StandardAnalyzerTokenizer();
		for(String categoryKeyword : topCategoryKeywords) {
			String opFilePath = dumpOutputFilePathPrefix + categoryKeyword + ".txt";
			System.out.println("Processing keyword : " + categoryKeyword + " in file " + opFilePath);
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(opFilePath)));
			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			
			String currLine;
			StringBuilder itemBuilder = new StringBuilder();
			boolean isCategoryKeywordPresent = false;
			int numSampleItemPairsFound = 0;
			String itemPairName = null;
			while((currLine = reader.readLine()) != null) {
				currLine = currLine.trim();
				
				// empty line signals the end of the itempair
				if(currLine.isEmpty()) {
					if(!includedItemPairsSet.contains(itemPairName) && isCategoryKeywordPresent) {
						includedItemPairsSet.add(itemPairName);
						System.out.println("Adding new " + itemPairName);
						++numSampleItemPairsFound;
						itemPairName = null;
						writer.write(itemBuilder.toString());
						writer.newLine();
					}
					if(numSampleItemPairsFound == numSamplePairsExpected) {
						writer.close();
						System.out.println("Wrote file " + opFilePath + " successfully");
						break;
					}
					
					isCategoryKeywordPresent = false;
					itemBuilder = new StringBuilder();
				}
				// keep storing the current item
				else {
					// name of the itempair
					if(currLine.startsWith(Constants.ID)) {
						itemPairName = currLine;
						//includedItemPairsSet.add(itemPairName);
						//System.out.println("Added new itempair to set " + itemPairName);
					}
					if(currLine.startsWith(CATEGORY_ATTRIBUTE)) {
						List<String> tokens = tokenizer.tokenize(currLine);
						if(tokens.contains(categoryKeyword)) {
							isCategoryKeywordPresent = true;
						}
					}
					
					currLine = cleanupString(currLine);
					currLine = removeStopWords(currLine);
					itemBuilder.append(currLine).append("\n");
				}
			}
			
			reader.close();
		}
		
	}
	
	// Remove some special characters from the string/
	// This hack is done because the initial data dump given had the old delimiter led to some issues.
	// Some migrate this data to the new delimiter.
	private static String cleanupString(String currLine)
	{
		if(currLine.contains("^A") || currLine.contains("\001")) {
			currLine = currLine.replaceAll("^A", ";#");
			currLine = currLine.replaceAll("\001", ";#");
		}

		return currLine;
	}
	
	// Remove stop words from some key attributes like category and brand name etc.
	private static String removeStopWords(String currLine)
	{
		if(currLine.contains(CATEGORY_ATTRIBUTE)) {
			for(String stopword : categoryStopwords) {
				if(currLine.contains(stopword)) {
					currLine = currLine.replaceAll(stopword, "");					
				}
			}
		}
		if(currLine.contains(BRAND_ATTRIBUTE)) {
			for(String stopword : brandStopwords) {
				if(currLine.contains(stopword)) {
					currLine = currLine.replaceAll(stopword, "");					
				}
			}			
		}
		
		return currLine;
	}
	
	// Finds the most common category keywords in the file
	private static List<String> getFrequentCategories(double expectedCategoryPercent, File dataFile) throws IOException
	{
		System.out.println("Generating the categories ..");
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));
		String currLine;

		StandardAnalyzerTokenizer tokenizer = new StandardAnalyzerTokenizer();
		Map<String, Integer> topCategoryKeywordsMap = Maps.newHashMap();
		int totalItemPairs = 0;
		while((currLine = reader.readLine()) != null) {
			if(!currLine.startsWith(CATEGORY_ATTRIBUTE)) {
				continue;
			}
			
			++totalItemPairs;
			currLine = currLine.replaceAll(CATEGORY_ATTRIBUTE, "");
			currLine = DataCleanupUtils.cleanUpString(currLine);
			List<String> tokens = tokenizer.tokenize(currLine);
			for(String token : tokens) {
				if(categoryStopwords.contains(token) || token.length() <= 3) {
					continue;
				}
				int keywordCount = 0;
				if(topCategoryKeywordsMap.containsKey(token)) {
					keywordCount = topCategoryKeywordsMap.get(token);
				}
				++keywordCount;
				
				topCategoryKeywordsMap.put(token, keywordCount);
			}
		}
		
		List<String> topCategoryKeywords = Lists.newArrayList();
		for(Map.Entry<String, Integer> entry : topCategoryKeywordsMap.entrySet()) {
			int keywordFrequency = entry.getValue();
			double percentage = ((keywordFrequency*100.0)/(totalItemPairs*1.0));
			if(Double.compare(percentage, DEFAULT_CATEGORY_PERCENT_OCCURENCE) > 0.0) {
				topCategoryKeywords.add(entry.getKey());
				System.out.println("Occurence percentage : " + percentage + " for keyword " + entry.getKey());
			}
		}
		
		return Lists.newArrayList(topCategoryKeywords);
	}
}
