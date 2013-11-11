package com.walmart.productgenome.pairComparison.utils.rule.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.audit.TokenAuditEntity;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntity;
import com.walmart.productgenome.pairComparison.model.rule.MatchEntityPair;
import com.walmart.productgenome.pairComparison.service.ItemPairDataFileReader;
import com.walmart.productgenome.pairComparison.utils.comparers.ComparersFactory;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;
import com.walmart.productgenome.pairComparison.utils.data.DataCleanupUtils;
import com.walmart.productgenome.pairComparison.utils.rule.calculator.StringTokenContainmentScoreCalculator;
import com.walmart.productgenome.pairComparison.utils.tokenizers.StandardAnalyzerTokenizer;

/**
 * Utility class that analyzes the data file and tries to come up with statistical analysis and a 
 * bunch of potential rules for this data. These rules would just be the initial recommendation and
 * should be manually analyzed and iterated to arrive at the final set of rules.
 * 
 * This is more of a POC to see if the rule development process can be semi-automated.
 * 
 * @author sprasa4
 *
 */
public class AutoRuleRecommender {
	private static Map<String, Integer> sourceItemAttrCountMap = Maps.newHashMap();
	private static Map<String, Integer> targetItemAttrCountMap = Maps.newHashMap();
	private static Map<String, Set<String>> sourceItemAttrUniqueCountMap = Maps.newHashMap();
	private static Map<String, Set<String>> targetItemAttrUniqueCountMap = Maps.newHashMap();
	
	private static Map<String, Map<String, Double>> sourceAttrToTargetAttrsMap = Maps.newHashMap();
	private static Map<String, Map<String, Double>> targetAttrToSourceAttrsMap = Maps.newHashMap();
	
	private static List<String> sourceAttrsToAnalyze = 
		Lists.newArrayList(
			"req_upc_10", "req_upc_11", "req_upc_12", "req_upc_13", "req_upc_14", 
			"req_category", "req_part_number", "req_brand_name", "pd_title_number_units",
			"pd_title_variation_phrases", "req_color", "extracted_color", "req_isbn_13", "req_author",
			"req_publisher", "req_binding");
	
	// target attr recommendation variables
	private static List<IComparer> comparers = ComparersFactory.getComparers(Constants.FUZZY_STRING_COMPARER);
	private static StandardAnalyzerTokenizer tokenizer = new StandardAnalyzerTokenizer();
	private static StringTokenContainmentScoreCalculator calculator = new StringTokenContainmentScoreCalculator();
	private static Double TARGET_ATTR_INCLUSION_THRESHOLD = 10.0;
	
	// data stats generation variables
	private static int totalItemPairs = 0;
	private static String sourceDS;
	private static String targetDS;
	
	// List of rule suggestions
	private static List<AttributeValueAnalysis> allAttrValueAnalysis = Lists.newArrayList();
	
	public static void main(String[] args)
	{
		String dirPath = "/Users/sprasa4/Desktop/wse_stores_golden_ds";
		File dirFile = new File(dirPath);
		File[] allFiles = dirFile.listFiles();
		for(File file : allFiles) {
			processDataForRuleGeneration(file);
			System.out.println("Processed file : "  + file);			
		}
		
		/*
		for(int i=0; i <= 1; i++) {
			String dataFilePath = "/Users/sprasa4/Data/Work/resources/data/wse_stores_data/WSE_STORES_" + i;
			File dataFile = new File(dataFilePath);
			processDataForRuleGeneration(dataFile);
			System.out.println("Processed file : "  + dataFilePath);
		}
		*/
		
		
		generateRuleRecommendations();		
		for(AttributeValueAnalysis attrValueAnalysis : allAttrValueAnalysis) {
			System.out.println(attrValueAnalysis.toString());		
		}
		
		// write the output to excel file
		writeRuleSuggestionsToFile("WSE_Stores_Rules_Golden_DS");
	}
	
	/**
	 * Generates statistical analysis and initial set of recommendation rules by reading a 
	 * data file containing itempairs in the proper data format.
	 * @param dataFile	Data file containing itempairs that needs to be analyzed
	 */
	public static void processDataForRuleGeneration(File dataFile)
	{
		List<MatchEntityPair> itemPairs = ItemPairDataFileReader.getDataFileItemPairs(dataFile);
		totalItemPairs += itemPairs.size();
		analyzeItempairStats(sourceAttrsToAnalyze, itemPairs);
		findEquivalentTargetAttrs(sourceAttrsToAnalyze, itemPairs, sourceAttrToTargetAttrsMap, true);
		findEquivalentTargetAttrs(sourceAttrsToAnalyze, itemPairs, targetAttrToSourceAttrsMap, false);
	}
	 
	// Generates statistical analysis of all the itempairs
	private static void analyzeItempairStats(List<String> sourceAttrsToAnalyze, List<MatchEntityPair> itemPairs)
	{
		for(MatchEntityPair itemPair : itemPairs) {
			MatchEntity sourceItem = itemPair.getSourceItem();
			MatchEntity targetItem = itemPair.getTargetItem();
			
			sourceDS = sourceItem.getSource();
			targetDS = targetItem.getSource();
			
			Map<String, Set<String>> sourceAttrValueMap = sourceItem.getAttributeNameValueSetMap();
			Map<String, Set<String>> targetAttrValueMap = targetItem.getAttributeNameValueSetMap();
			
			updateAttrStatsMap(sourceAttrsToAnalyze, sourceAttrValueMap, sourceItemAttrCountMap, sourceItemAttrUniqueCountMap);
			updateAttrStatsMap(sourceAttrsToAnalyze, targetAttrValueMap, targetItemAttrCountMap, targetItemAttrUniqueCountMap);
		}
	}
	
	/**
	 * Updates the global stats of the attributes using the attribute data from the current itempair
	 * @param itemAttrValueMap
	 * @param itemAttrCountMap
	 * @param itemAttrUniqueCountMap
	 */
	private static void updateAttrStatsMap(
		List<String> sourceAttrsToAnalyze,
		Map<String, Set<String>> itemAttrValueMap, 
		Map<String, Integer> itemAttrCountMap,
		Map<String, Set<String>> itemAttrUniqueCountMap)
	{
		for(Map.Entry<String, Set<String>> entry : itemAttrValueMap.entrySet()) {
			String attrName = entry.getKey();
			if(!sourceAttrsToAnalyze.contains(attrName)) {
				continue;
			}
			Set<String> rawAttrValues = entry.getValue();
			Set<String> attrValues = getCleanedUpAttrValues(rawAttrValues);
			
			// Skip processing in case of null attr name or absent attr values
			if(StringUtils.isBlank(attrName) || CollectionUtils.isEmpty(rawAttrValues)) {
				continue;
			}
			
			int attrOccurrenceCount = 0;
			Set<String> uniqueValuesForAttrName = null;
			if(itemAttrCountMap.containsKey(attrName)) {
				attrOccurrenceCount = itemAttrCountMap.get(attrName);
				uniqueValuesForAttrName = itemAttrUniqueCountMap.get(attrName);
			}
			else {
				uniqueValuesForAttrName = Sets.newHashSet();
			}

			attrOccurrenceCount += 1;
			uniqueValuesForAttrName.add(attrValues.toString());

			itemAttrCountMap.put(attrName, attrOccurrenceCount);
			itemAttrUniqueCountMap.put(attrName, uniqueValuesForAttrName);
		}		
	}
	
	// Determines the target attributes that contain the tokens of source attributes. These are the
	// potential attributes that should be included in the list of target attributes to search for
	// source attributes
	private static void findEquivalentTargetAttrs(
		List<String> attrsToAnalyze, List<MatchEntityPair> itemPairs,
		Map<String, Map<String, Double>> sourceAttrToSimilarTargetAttrsMap, boolean isMatchFromSourceToTarget)
	{
		for(MatchEntityPair itemPair : itemPairs) {
			for(String attrName : attrsToAnalyze) {
				MatchEntity sourceItem = null;
				MatchEntity targetItem =null;
				if(isMatchFromSourceToTarget) {
					sourceItem = itemPair.getSourceItem();
					targetItem = itemPair.getTargetItem();					
				}
				else {
					sourceItem = itemPair.getTargetItem();
					targetItem = itemPair.getSourceItem();
				}
				
				Map<String, Set<String>> sourceAttrValueMap = sourceItem.getAttributeNameValueSetMap();
				Map<String, Set<String>> targetAttrValueMap = targetItem.getAttributeNameValueSetMap();
				
				if(sourceAttrValueMap.containsKey(attrName)) {
					generateTargetAttrMatchesForSourceAttr(attrName, sourceAttrValueMap, targetAttrValueMap, sourceAttrToSimilarTargetAttrsMap);
				}
			}			
		}

	}
	
	private static void generateTargetAttrMatchesForSourceAttr(
		String attrName, Map<String, Set<String>> sourceAttrValueMap, Map<String, Set<String>> targetAttrValueMap,
		Map<String, Map<String, Double>> sourceAttrToSimilarTargetAttrsMap)
	{
		Map<String, Double> targetAttrsTokenMatchedMap = Maps.newHashMap();
		Set<String> sourceAttrValues = sourceAttrValueMap.get(attrName);
		
		// Collect all the source value tokens
		Set<String> sourceAttrValuesTokens = Sets.newHashSet();
		for(String sourceAttrValue : sourceAttrValues) {
			List<String> sourceValueTokens = tokenizer.tokenize(sourceAttrValue);
			sourceAttrValuesTokens.addAll(sourceValueTokens);
		}
		List<String> sourceAttrValuesTokensList = Lists.newArrayList(sourceAttrValuesTokens);
		sourceAttrValuesTokensList = DataCleanupUtils.removeNullOrEmptyStrings(sourceAttrValuesTokensList);
		
		for(Map.Entry<String, Set<String>> entry : targetAttrValueMap.entrySet()) {
			String targetAttrName = entry.getKey();
			Set<String> targetAttrValues = entry.getValue();
			Set<String> targetValueTokens = Sets.newHashSet();
			for(String targetAttrValue : targetAttrValues) {
				List<String> targetAttrValueTokens = tokenizer.tokenize(targetAttrValue);
				if(CollectionUtils.isNotEmpty(targetAttrValueTokens)) {
					targetValueTokens.addAll(targetAttrValueTokens);
				}
			}
			List<String> targetAttrValuesTokenList = Lists.newArrayList(targetValueTokens);
			
			double matchingScore = findMatchingScore(sourceAttrValuesTokensList, targetAttrValuesTokenList, comparers);
			if(Double.compare(matchingScore, 0.0) > 0) {
				double totalMatchedScoreForTargetAttr = 0;
				if(targetAttrsTokenMatchedMap.containsKey(targetAttrName)) {
					totalMatchedScoreForTargetAttr = targetAttrsTokenMatchedMap.get(targetAttrName);
				}
				
				totalMatchedScoreForTargetAttr += matchingScore;
				targetAttrsTokenMatchedMap.put(targetAttrName, totalMatchedScoreForTargetAttr);
			}
		}
		
		// merge the target attrs for this source attr in the global map
		mergeTargetAttrsFoundForSourceAttrs(attrName, targetAttrsTokenMatchedMap, sourceAttrToSimilarTargetAttrsMap);
	}
	
	// Determines how many source tokens match with target tokens
	private static double findMatchingScore(List<String> sourceTokens, List<String> targetTokens, List<IComparer> comparers)
	{
		List<TokenAuditEntity> tokenAuditValues = Lists.newArrayList();
		List<List<String>> targetTokenValuesList = Lists.newArrayList();
		targetTokenValuesList.add(targetTokens);
		
		double matchingScore = calculator.containmentScore(sourceTokens, targetTokenValuesList, comparers, tokenAuditValues);
		return matchingScore;
	}
	
	private static void mergeTargetAttrsFoundForSourceAttrs(
		String sourceAttrName, Map<String, Double> targetAttrsTokenMatchedMap,
		Map<String, Map<String, Double>> sourceAttrToSimilarTargetAttrsMap)
	{
		Map<String, Double> targetAttrScoreMap = null;
		if(sourceAttrToSimilarTargetAttrsMap.containsKey(sourceAttrName)) {
			targetAttrScoreMap = sourceAttrToSimilarTargetAttrsMap.get(sourceAttrName);
		}
		
		for(Map.Entry<String, Double> entry : targetAttrsTokenMatchedMap.entrySet()) {
			String targetAttrName = entry.getKey();
			Double targetAttrMatchScore = entry.getValue();
			
			Double totalTargetAttrScore = 0.0;
			if(MapUtils.isNotEmpty(targetAttrScoreMap)) {
				if(targetAttrScoreMap.containsKey(targetAttrName)) {
					totalTargetAttrScore = targetAttrScoreMap.get(targetAttrName);
				}
			}
			else {
				targetAttrScoreMap = Maps.newHashMap();
			}
			
			totalTargetAttrScore += targetAttrMatchScore;
			targetAttrScoreMap.put(targetAttrName, totalTargetAttrScore);
		}
		
		sourceAttrToSimilarTargetAttrsMap.put(sourceAttrName, targetAttrScoreMap);
	}
	
	private static void generateRuleRecommendations()
	{
		for(Map.Entry<String, Integer> entry : sourceItemAttrCountMap.entrySet()) {
			String attrName = entry.getKey();
			int attrCountInSourceItems = entry.getValue();
			int attrCountInTargetItems = 0;
			if(targetItemAttrCountMap.containsKey(attrName)) {
				attrCountInTargetItems = targetItemAttrCountMap.get(attrName);
			}
			
			// Frequency statistics
			int maxOccurenceCountAttr = (attrCountInSourceItems > attrCountInTargetItems) ? attrCountInSourceItems : attrCountInTargetItems;
			double percentOccurence = (maxOccurenceCountAttr*100)/(double)totalItemPairs;
			double sourcePercentOccurence = (attrCountInSourceItems*100)/(double)totalItemPairs;
			double targetPercentOccurence = (attrCountInTargetItems*100)/(double)totalItemPairs;

			// Uniqueness statistics
			int sourceAttrValueUniqueCount = 0;
			int targetAttrValueUniqueCount = 0;
			if(sourceItemAttrUniqueCountMap.containsKey(attrName)) {
				sourceAttrValueUniqueCount = sourceItemAttrUniqueCountMap.get(attrName).size();				
			}
			if(targetItemAttrUniqueCountMap.containsKey(attrName)) {
				targetAttrValueUniqueCount = targetItemAttrUniqueCountMap.get(attrName).size();
			}
			
			double sourceAttrValuesUniquePercent = (sourceAttrValueUniqueCount * 100)/(double)totalItemPairs;
			double targetAttrValuesUniquePercent = (targetAttrValueUniqueCount * 100)/(double)totalItemPairs;
	
			// Set data stats for attribute name
			AttributeValueAnalysis ruleSuggestForAttr = new AttributeValueAnalysis();
			ruleSuggestForAttr.setAttrName(attrName);
			ruleSuggestForAttr.setSourceItemDataSource(sourceDS);
			ruleSuggestForAttr.setTargetItemDataSource(targetDS);
			ruleSuggestForAttr.setGlobalOccurencePercent(percentOccurence);
			ruleSuggestForAttr.setSourceOccurencePercent(sourcePercentOccurence);
			ruleSuggestForAttr.setTargetOccurencePercent(targetPercentOccurence);
			ruleSuggestForAttr.setSourceUniqueValuesPercent(sourceAttrValuesUniquePercent);
			ruleSuggestForAttr.setTargetUniqueValuesPercent(targetAttrValuesUniquePercent);

			// Get attribute recommendation stats for source to target items
			if(sourceAttrToTargetAttrsMap.containsKey(attrName)) {
				List<String> targetAttrsToIncludeForSourceAttr = Lists.newArrayList();
				Map<String, Double> targetAttrsMap = sourceAttrToTargetAttrsMap.get(attrName);
				for(Map.Entry<String, Double> entry2: targetAttrsMap.entrySet()) {
					double targetAttrMatchScore = entry2.getValue();
					double matchPercent = (targetAttrMatchScore*100)/(double)totalItemPairs;
					if(Double.compare(matchPercent, TARGET_ATTR_INCLUSION_THRESHOLD) > 0) {
						targetAttrsToIncludeForSourceAttr.add(entry2.getKey() + "(" + matchPercent +"%)");					
					}
				}
				ruleSuggestForAttr.setRelevantAttrsMapFromSourceToTarget(targetAttrsToIncludeForSourceAttr);
			}

			// Get attribute recommendation stats for target to source items
			if(targetAttrToSourceAttrsMap.containsKey(attrName)) {
				List<String> targetAttrsToIncludeForSourceAttr = Lists.newArrayList();
				Map<String, Double> targetAttrsMap = targetAttrToSourceAttrsMap.get(attrName);
				for(Map.Entry<String, Double> entry2: targetAttrsMap.entrySet()) {
					double targetAttrMatchScore = entry2.getValue();
					double matchPercent = (targetAttrMatchScore*100)/(double)totalItemPairs;
					if(Double.compare(matchPercent, TARGET_ATTR_INCLUSION_THRESHOLD) > 0) {
						targetAttrsToIncludeForSourceAttr.add(entry2.getKey());					
					}
				}
				ruleSuggestForAttr.setRelevantAttrsMapFromTargetToSource(targetAttrsToIncludeForSourceAttr);
			}

			allAttrValueAnalysis.add(ruleSuggestForAttr);
		}		
	}
	
	private static void writeRuleSuggestionsToFile(String workbookName)
	{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(workbookName);
		
		int rownum=0;
		Row titleRow = sheet.createRow(0);
		
		for(AttributeValueAnalysis ruleSuggest : allAttrValueAnalysis) {
			Row ruleRow = sheet.createRow(++rownum);
			
			int cellnum=-1;
			
			Cell attrNameCell = ruleRow.createCell(++cellnum); 
			attrNameCell.setCellValue(ruleSuggest.getAttrName());
			
			Cell overallPercentCell = ruleRow.createCell(++cellnum); 
			overallPercentCell.setCellValue((Double)ruleSuggest.getGlobalOccurencePercent());

			Cell sourcePercentCell = ruleRow.createCell(++cellnum); 
			sourcePercentCell.setCellValue((Double)ruleSuggest.getSourceOccurencePercent());

			Cell targetPercentCell = ruleRow.createCell(++cellnum); 
			targetPercentCell.setCellValue((Double)ruleSuggest.getTargetOccurencePercent());

			Cell sourceUniquePercentCell = ruleRow.createCell(++cellnum); 
			sourceUniquePercentCell.setCellValue((Double)ruleSuggest.getSourceUniqueValuesPercent());

			Cell targetUniquePercentCell = ruleRow.createCell(++cellnum); 
			targetUniquePercentCell.setCellValue((Double)ruleSuggest.getTargetUniqueValuesPercent());

			Cell sourceToTargetAttrsSuggest = ruleRow.createCell(++cellnum); 
			if(CollectionUtils.isNotEmpty(ruleSuggest.getRelevantAttrsMapFromSourceToTarget())) {
				sourceToTargetAttrsSuggest.setCellValue(ruleSuggest.getRelevantAttrsMapFromSourceToTarget().toString());				
			}
			else {
				sourceToTargetAttrsSuggest.setCellValue("NA");
			}

			Cell targetToSourceAttrsSuggest = ruleRow.createCell(++cellnum); 
			if(CollectionUtils.isNotEmpty(ruleSuggest.getRelevantAttrsMapFromTargetToSource())) {
				targetToSourceAttrsSuggest.setCellValue(ruleSuggest.getRelevantAttrsMapFromTargetToSource().toString());				
			}
			else {
				targetToSourceAttrsSuggest.setCellValue("NA");
			}
		}

		try {
		    FileOutputStream out = new FileOutputStream(new File("/Users/sprasa4/Desktop/" + workbookName + "_golden_ds.xls"));
		    workbook.write(out);
		    out.close();
		    System.out.println("Excel written successfully..");
		     
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	// Removes NULL, empty or "null" tokens
	private static Set<String> getCleanedUpAttrValues(Set<String> attrValues)
	{
		return Sets.newHashSet(Lists.newArrayList(attrValues));
	}
}
