package com.walmart.productgenome.pairComparison;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.walmart.productgenome.pairComparison.model.Constants;

public class DatasetMismatchItemPairsGenerator {
	
	private static String filePathPrefix = "/Users/sprasa4/Data/Work/resources/data/wse_cnet_mismatch/20120731/";
	
	private static String dataFileNewPath = filePathPrefix + "WALMART_SEARCH_EXTRACT_CNET-pairs-new-rules-true-with-amalgammated-attribute.txt";
	private static String dataFileOldPath = filePathPrefix + "WALMART_SEARCH_EXTRACT_CNET-pairs-new-rules-ALL_ITEMPAIRS_true.txt";

	private static String NEW_RULE_EXTRA_ITEMPAIRS = "NEW";
	private static String OLD_RULE_EXTRA_ITEMPAIRS = "OLD";
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Finding the diff itempairs ..");
		Map<String, Set<String>> diffItempairsMap = getDiffItempairsMap();
		
		// Dumping the extra new matching itempairs found
		System.out.println("Dumping new extra itempairs ..");
		dumpDiffItemPairs(diffItempairsMap.get(NEW_RULE_EXTRA_ITEMPAIRS), dataFileNewPath, filePathPrefix + "new_rule_extra_itempairs.txt");
		
		// Dumping the old matching itempairs
		System.out.println("Dumping old extra itempairs ..");
		dumpDiffItemPairs(diffItempairsMap.get(OLD_RULE_EXTRA_ITEMPAIRS), dataFileOldPath, filePathPrefix + "old_rule_extra_itempairs.txt");
	}

	// Dump these itempairs to a temporary file
	private static void dumpDiffItemPairs(Set<String> itempairsToSearch, String fileToRead, String fileToWrite) throws IOException
	{
		File writeFile = new File(fileToWrite);
		File readFile = new File(fileToRead);

		Set<String> itempairsAlreadyWritten = Sets.newHashSet();
		BufferedReader br = new BufferedReader(new FileReader(readFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(writeFile));
		
		String readLine;
		while((readLine=br.readLine()) != null) {
			// start of a new itempair
			if(readLine.startsWith(Constants.ID)) {
				String itempairName = getItempairName(readLine);
				boolean isItempairToWrite = checkIfItempairToBeWritten(itempairName, itempairsToSearch, itempairsAlreadyWritten);
				
				// Dump this itempair to the file
				if(isItempairToWrite) {
					// write the ID line
					bw.write(readLine);
					bw.newLine();
					
					// keep writing till a blank line is not found
					while(true) {
						readLine = br.readLine();
						if(readLine == null) {
							break;
						}
						if(readLine.contains("^A") || readLine.contains("\001")) {
							readLine = readLine.replaceAll("^A", ";#");
							readLine = readLine.replaceAll("\001", ";#");
						}

						// Write a blank row delimiter
						if(readLine.isEmpty()) {
							bw.newLine();
							break;
						}
						
						bw.write(readLine);
						bw.newLine();
					} // end of while block
					
					itempairsAlreadyWritten.add(itempairName);
				} // end of itempair write
			} // end of check for ID line
		} // end of file read
		
		br.close();
		bw.close();
		
		System.out.println("Wrote " + itempairsAlreadyWritten.size() + " itempairs to the file ..");
	}
	
	private static boolean checkIfItempairToBeWritten(String itempairName, Set<String> itempairsToSearch, Set<String> itempairsAlreadyWritten)
	{
		boolean isItempairToBeWritten = true;
		// Either it is not a diff itempair or has already been written out, then just skip
		if(!itempairsToSearch.contains(itempairName) || (itempairsAlreadyWritten.contains(itempairName))) 
		{
			return false;
		}
		
		return isItempairToBeWritten;
	}
	
	private static String getItempairName(String line)
	{
		String[] idTokens = line.split("\\|#");
		
		String firstItem = idTokens[1].trim();
		String secondItem = idTokens[2].trim();

		String itempairName = null;
		if(firstItem.compareTo(secondItem) <= 0) {
			itempairName = firstItem + "#" + secondItem;
		}
		else {
			itempairName = secondItem + "#" + firstItem;
		}
		
		return itempairName;
	}
	
	// Gets the different itempairs in two datasets
	private static Map<String, Set<String>> getDiffItempairsMap() throws IOException
	{
		File newFile = new File(dataFileNewPath);
		File oldFile = new File(dataFileOldPath);
		
		Set<String> newMatchedItempairs = getItempairs(newFile);
		Set<String> oldMatchedItempairs = getItempairs(oldFile);

		Set<String> diffInNewPairs = Sets.difference(newMatchedItempairs, oldMatchedItempairs);
		Set<String> diffInOldPairs = Sets.difference(oldMatchedItempairs, newMatchedItempairs);
		
		int commonItemPairs = 0;
		for(String newItemPair : newMatchedItempairs) {
			if(oldMatchedItempairs.contains(newItemPair)) {
				++commonItemPairs;
			}
		}
		/*
		System.out.println("New matched itempairs : " + newMatchedItempairs.size());
		System.out.println("Old matched itempairs : " + oldMatchedItempairs.size());
		System.out.println("Extra NEW itempairs  : " + diffInNewPairs.size());
		System.out.println("Extra OLD itempairs  : " + diffInOldPairs.size());
		System.out.println("Common itempairs : " + commonItemPairs);

		Set<String> extraInNew = Sets.newHashSet();
		for(String newItemPair : newMatchedItempairs) {
			if(!oldMatchedItempairs.contains(newItemPair)) {
				System.out.println(newItemPair);
				extraInNew.add(newItemPair);
			}
		}
		System.out.println("########### New Extra ######## " + extraInNew.size());
		
		Set<String> extraInOld = Sets.newHashSet();
		for(String oldItemPair : oldMatchedItempairs) {
			if(!newMatchedItempairs.contains(oldItemPair)) {
				System.out.println(oldItemPair);
				extraInOld.add(oldItemPair);
			}
		}
		System.out.println("########### Old Extra ######## " + extraInOld.size());

		
		Set<String> diffItempairs = Sets.newHashSet();
		diffItempairs.addAll(diffInNewPairs);
		diffItempairs.addAll(diffInOldPairs);
		*/
		
		Map<String, Set<String>> diffItempairsMap = Maps.newHashMap();
		diffItempairsMap.put(NEW_RULE_EXTRA_ITEMPAIRS, diffInNewPairs);
		diffItempairsMap.put(OLD_RULE_EXTRA_ITEMPAIRS, diffInOldPairs);
		return diffItempairsMap;
	}
	
	private static Set<String> getItempairs(File dataFile) throws IOException
	{
		Set<String> itempairs = Sets.newHashSet();
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		String currLine;
		int idCount = 0;
		Map<String, Integer> itemPairCountMap = Maps.newHashMap();
		while ((currLine = br.readLine()) != null) {
			if(currLine.startsWith(Constants.ID)) {
				++idCount;
				String[] idTokens = currLine.split("\\|#");
				
				String firstItem = idTokens[1].trim();
				String secondItem = idTokens[2].trim();
				
				String itempairName = null;
				if(firstItem.compareTo(secondItem) <= 0) {
					itempairName = firstItem + "#" + secondItem;
				}
				else {
					itempairName = secondItem + "#" + firstItem;
				}
				itempairs.add(itempairName);
				
				int itempairFreq = 0;
				if(itemPairCountMap.containsKey(itempairName)) {
					itempairFreq = itemPairCountMap.get(itempairName);
				}
				
				itempairFreq++;
				itemPairCountMap.put(itempairName, itempairFreq);
			}
		}

		/*
		System.out.println("ID count file : " + idCount + ", items count : " + itempairs.size());
		
		for(Map.Entry<String, Integer> entry : itemPairCountMap.entrySet()) {
			if(entry.getValue() > 0) {
				System.out.println(entry.getKey() + " ===> " + entry.getValue());
			}
		}
		*/
		
		return itempairs;
	}
}
