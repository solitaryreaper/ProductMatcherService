package com.walmart.productgenome.pairComparison.tools;

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
	private static String CATEGORY_ATTRIBUTE = "req_category";	
	private static Set<String> stopwords = Sets.newHashSet("total", "all", "unassigned", "special", "general");
	
	/**
	 * Use tool as follows :
	 * 
	 * java GoldenDataSetSampleGenerator "/Users/sprasa4/Data/Work/resources/data/wse_stores_data/WSE_STORES_0" /Users/sprasa4/Data/Work/resources/data/golden_data_wse_stores/WSE_STORES_GOLDEN_DS_" 3 25
	 */
	public static void main(String[] args)
	{
		if(args.length < 2 || args.length > 4) {
			System.out.println("Incorrect number of arguments.");
			System.out.println("java GoldenDataSetSampleGenerator <inputDataFilePath> <outputDataFilesPathPrefix> <categoryPercentOccurenceExpected> <numItemsPerCategoryRequired>");
			System.exit(1);
		}
		
		String inputDataFile = args[0];
		String dumpOutputFilePathPrefix = args[1];
		double categoryPercentOccurenceExpected = Double.parseDouble(args[2]);
		int numItemsPerCategoryRequired = Integer.parseInt(args[3]);
		
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
		
		dumpSampleItemPairsForEachCategory(topCategoryKeywords, dataFile, outputDataFilePrefix, numItemPairsPerCategory);
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
			System.out.println("Processing keyword : " + categoryKeyword);
			String opFilePath = dumpOutputFilePathPrefix + categoryKeyword + ".txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(opFilePath)));
			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			
			String currLine;
			StringBuilder itemBuilder = new StringBuilder();
			boolean isCategoryKeywordPresent = false;
			int numSampleItemPairsFound = 0;
			while((currLine = reader.readLine()) != null) {
				currLine = currLine.trim();
				
				// empty line signals the end of the itempair
				if(currLine.isEmpty()) {
					if(isCategoryKeywordPresent) {
						++numSampleItemPairsFound;
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
					if(currLine.startsWith(CATEGORY_ATTRIBUTE)) {
						List<String> tokens = tokenizer.tokenize(currLine);
						if(tokens.contains(categoryKeyword)) {
							isCategoryKeywordPresent = true;
						}
					}
					itemBuilder.append(currLine).append("\n");
				}
			}
			
			reader.close();
		}
		
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
				if(stopwords.contains(token) || token.length() <= 3) {
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
			if(Double.compare(percentage, expectedCategoryPercent) > 0.0) {
				topCategoryKeywords.add(entry.getKey());
				System.out.println("Occurence percentage : " + percentage + " for keyword " + entry.getKey());
			}
		}
		
		return Lists.newArrayList(topCategoryKeywords);
	}
}
