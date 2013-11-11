package com.walmart.productgenome.pairComparison.utils.comparers;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.utils.comparers.numericComparers.DifferenceComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.numericComparers.ExactNumericComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.numericComparers.IntToEnglishWordComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.numericComparers.RatioComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.ConsonantsOnlyComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.DomainSpecificComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.ExactComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.LevenshteinDistanceBasedComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.PrefixBasedComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.StemmedComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.SuffixBasedComparer;
import com.walmart.productgenome.pairComparison.utils.comparers.stringComparers.WalmartSpecificBrandsComparer;

public class ComparersFactory {

	//Available String comparers
	private static final IComparer EXACT_STRING = new ExactComparer();
	private static final IComparer STEMMED_STRING = new StemmedComparer();
	private static final IComparer PREFIX_STRING = new PrefixBasedComparer();
	private static final IComparer SUFFIX_STRING = new SuffixBasedComparer();
	private static final IComparer CONSONANTS_ONLY_STRING = new ConsonantsOnlyComparer();
	private static final IComparer DOMAIN_SPECIFIC_KEYWORDS = new DomainSpecificComparer();
	private static final IComparer WALMART_BRANDS_ONLY = new WalmartSpecificBrandsComparer();
	
	//Available Integer comparers
	private static final IComparer EXACT_INTEGER = new ExactNumericComparer();
	private static final IComparer DIFF_INTEGER = new DifferenceComparer();
	private static final IComparer RATIO_INTEGER = new RatioComparer();
	private static final IComparer INT2ENGLISH = new IntToEnglishWordComparer();
	
	//Available Double comparers
	private static final IComparer EXACT_DOUBLE =  new ExactNumericComparer();
	private static final IComparer DIFF_DOUBLE = new DifferenceComparer();
	private static final IComparer RATIO_DOUBLE = new RatioComparer();
	
	/**
	 * Group the comparers into logic blocks here
	 */
	// -------------- String comparers logical grouping ------------------------------
	private static final List<IComparer> STRING_COMPARERS = 
		Lists.newArrayList(EXACT_STRING, STEMMED_STRING, PREFIX_STRING, SUFFIX_STRING, 
				CONSONANTS_ONLY_STRING, INT2ENGLISH, DOMAIN_SPECIFIC_KEYWORDS, WALMART_BRANDS_ONLY);
	private static final List<IComparer> FUZZY_STRING_COMPARERS = 
		Lists.newArrayList(EXACT_STRING, STEMMED_STRING, PREFIX_STRING, SUFFIX_STRING, 
				CONSONANTS_ONLY_STRING, INT2ENGLISH, DOMAIN_SPECIFIC_KEYWORDS, WALMART_BRANDS_ONLY);
	private static final List<IComparer> EXACT_STRING_COMPARERS = 
		Lists.newArrayList(EXACT_STRING);
	
	// -------------- Integer comparers logical grouping ------------------------------
	private static final List<IComparer> INTEGER_COMPARERS = 
		Lists.newArrayList(EXACT_INTEGER, DIFF_INTEGER, RATIO_INTEGER, INT2ENGLISH);
	private static final List<IComparer> FUZZY_INTEGER_COMPARERS = 
		Lists.newArrayList(EXACT_INTEGER, DIFF_INTEGER, RATIO_INTEGER, INT2ENGLISH);
	private static final List<IComparer> EXACT_INTEGER_COMPARERS = 
		Lists.newArrayList(EXACT_INTEGER);
	
	// -------------- Double comparers logical grouping ------------------------------	
	private static final List<IComparer> DOUBLE_COMPARERS = 
		Lists.newArrayList(EXACT_DOUBLE, DIFF_DOUBLE, RATIO_DOUBLE);
	private static final List<IComparer> FUZZY_DOUBLE_COMPARERS = 
		Lists.newArrayList(EXACT_DOUBLE, DIFF_DOUBLE, RATIO_DOUBLE);
	private static final List<IComparer> EXACT_DOUBLE_COMPARERS = 
		Lists.newArrayList(EXACT_DOUBLE);

	// ------------- Other logical grouping ------------------------------------------
	private static final List<IComparer> EXACT_VALUE_COMPARERS = 
			Lists.newArrayList(EXACT_STRING, EXACT_INTEGER, EXACT_DOUBLE);

	private static final Map<String, List<IComparer>> COMPARERS_MAP = Maps.newHashMap();
	static {
		COMPARERS_MAP.put(Constants.EXACT_STRING_COMPARER, 		EXACT_STRING_COMPARERS);
		COMPARERS_MAP.put(Constants.FUZZY_STRING_COMPARER, 		FUZZY_STRING_COMPARERS);
		COMPARERS_MAP.put(Constants.EXACT_INTEGER_COMPARER, 	EXACT_INTEGER_COMPARERS);
		COMPARERS_MAP.put(Constants.FUZZY_INTEGER_COMPARER, 	FUZZY_INTEGER_COMPARERS);
		COMPARERS_MAP.put(Constants.EXACT_DOUBLE_COMPARER, 		EXACT_DOUBLE_COMPARERS);
		COMPARERS_MAP.put(Constants.FUZZY_DOUBLE_COMPARER, 		FUZZY_DOUBLE_COMPARERS);	
		COMPARERS_MAP.put(Constants.EXACT_VALUE_COMPARER, 		EXACT_VALUE_COMPARERS);
	}
	
	public static List<IComparer> getComparers(final String comparersType) {
		if (COMPARERS_MAP.containsKey(comparersType) == false)
			throw new IllegalArgumentException("comparers type cannot be null");
		
		return COMPARERS_MAP.get(comparersType);
	}
	
	/**
	 * Returns the specific subtype of the comparers in the list of comparers. For example,
	 * it will return detailed subgroups like whether it is FUZZY STRING comparer or EXACT STRING
	 * comparer.
	 * 
	 * @param comparers
	 * @return
	 */
	public static String getComparersSubType(List<IComparer> comparers)
	{
		String comparersType = null;
		for(Map.Entry<String, List<IComparer>> entry : COMPARERS_MAP.entrySet()) {
			List<IComparer> value = entry.getValue();
			if(value == comparers) {
				comparersType = entry.getKey();
			}
		}
		
		return comparersType;
	}
	
	/**
	 * Returns generic type of the comparers in the list of comparers. For example, it will return
	 * generic groups like STRING comparers, INTEGER comparers etc. All comparers in a list should
	 * be of the same type, else an exception is thrown.
	 * @param comparers
	 * @return
	 */
	public static Map<String, List<IComparer>> getComparersByType(List<IComparer> comparers)
	{
		Map<String, List<IComparer>> comparersByType = Maps.newHashMap();
		List<IComparer> stringComparers = Lists.newArrayList();
		List<IComparer> integerComparers = Lists.newArrayList();
		List<IComparer> doubleComparers = Lists.newArrayList();
		
		for(IComparer comparer : comparers) {
			if(STRING_COMPARERS.contains(comparer)) {
				stringComparers.add(comparer);
			}
			else if(INTEGER_COMPARERS.contains(comparer)) {
				integerComparers.add(comparer);
			}
			else if(DOUBLE_COMPARERS.contains(comparer)) {
				doubleComparers.add(comparer);
			}
		}
		
		if(CollectionUtils.isNotEmpty(stringComparers)) {
			comparersByType.put(Constants.STRING_COMPARER, stringComparers);
		}
		if(CollectionUtils.isNotEmpty(integerComparers)) {
			comparersByType.put(Constants.INTEGER_COMPARER, integerComparers);
		}
		if(CollectionUtils.isNotEmpty(doubleComparers)) {
			comparersByType.put(Constants.DOUBLE_COMPARER, doubleComparers);
		}
		
		return comparersByType;
	}
	
	// Returns a short name for the comparer
	public static String getComparerShortName(String comparerClassName)
	{
		Map<String, String> comparersMap = Maps.newHashMap();
		comparersMap.put(PrefixBasedComparer.class.getSimpleName(), "PREFIX_COMPARER");
		comparersMap.put(SuffixBasedComparer.class.getSimpleName(), "SUFFIX_COMPARER");
		comparersMap.put(ExactComparer.class.getSimpleName(), "EXACT_COMPARER");
		comparersMap.put(StemmedComparer.class.getSimpleName(), "STEMMED_COMPARER");
		comparersMap.put(ConsonantsOnlyComparer.class.getSimpleName(), "CONSONANTS_ONLY_COMPARER");
		comparersMap.put(WalmartSpecificBrandsComparer.class.getSimpleName(), "WALMART_BRANDS_COMPARER");
		comparersMap.put(DomainSpecificComparer.class.getSimpleName(), "DOMAIN_SPECIFIC_WORDS_COMPARER");
		comparersMap.put(IntToEnglishWordComparer.class.getSimpleName(), "INT_TO_ENGLISH_COMPARER");
		
		return comparersMap.get(comparerClassName);
	}
}
