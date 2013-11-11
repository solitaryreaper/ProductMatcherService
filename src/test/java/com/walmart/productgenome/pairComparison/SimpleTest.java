package com.walmart.productgenome.pairComparison;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SimpleTest {
	private final static BiMap<String, String> domainSpecificSimilarWordsMap = HashBiMap.create();
	static {
		domainSpecificSimilarWordsMap.put("ws", "widescreen");
		domainSpecificSimilarWordsMap.put("laptop", "notebook");
		domainSpecificSimilarWordsMap.put("ff", "fullframe");
	}
	
	public static void main(String[] args)
	{
		System.out.println("Test " + domainSpecificSimilarWordsMap.containsKey("ws"));
		System.out.println("Test " + domainSpecificSimilarWordsMap.containsValue("widescreen"));
	}
}
