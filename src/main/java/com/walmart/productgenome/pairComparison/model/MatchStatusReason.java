package com.walmart.productgenome.pairComparison.model;

/**
 * Enum to categorise the various finer reasons because of which a source token matches with a target token.
 * @author sprasa4
 *
 */
public enum MatchStatusReason {
	ABBREVIATION_MATCH,
	CONCATENATION_MATCH,
	NO_CONTAINMENT,
	COMPARER_MATCH,
	
	// String Comparer matches
	EXACT_STRING_MATCH,	
	STEMMED_STRING_MATCH,
	PREFIX_STRING_MATCH,
	SUFFIX_STRING_MATCH,
	CONSONANTS_ONLY_STRING_MATCH,
	LEVENSHTEIN_DISTANCE_BASED_MATCH,
	
	// Integer Comparer Matches
	EXACT_INTEGER_MATCH,
	DIFF_INTEGER_MATCH,
	RATIO_INTEGER_MATCH,
	INT2ENGLISH_MATCH,
	INT2ROMANNUMERAL_MATCH,
	
	// Double Comparer Matches
	EXACT_DOUBLE_MATCH,
	DIFF_DOUBLE_MATCH,
	RATIO_DOUBLE_MATCH
}
