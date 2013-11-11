package com.walmart.productgenome.pairComparison.model;

/**
 * Language constructs for the new Domain Specific Language (DSL) defined for product matching.
 * The idea is to keep all the constants of this DSL at a single location and not spraying any
 * magic strings across the model objects.
 * 
 * @author sprasa4
 *
 */
public class Constants {
	
	// -------- AttributeMatchClauseMeta keyword name constructs -----------------------
	public static final String ATTRIBUTE_COMPARER 			= "COMPARER";
	public static final String SOURCE_TOKENIZER 			= "SOURCE_TOKENIZER";
	public static final String TARGET_TOKENIZER 			= "TARGET_TOKENIZER";
	public static final String CLAUSE_EVALUATOR				= "EVALUATOR";
	public static final String MISSING_ATTRIBUTE_ALLOWED 	= "MISSING_ATTRIBUTE_ALLOWED";
	public static final String SCORE_THRESHOLD 				= "SCORE";
	
	// -------- Evaluator factory constants -------
	public static final String ONE_WAY_EVALUATOR 			= "UNIDIRECTIONAL";
	public static final String TWO_WAY_EVALUATOR 			= "BIDIRECTIONAL";
	
	// -------- Tokenizer factory constants -------
	public static final String STANDARD_TOKENIZER 			= "STANDARD_ANALYZER";
	public static final String NO_TOKENIZER 				= "NONE";
	public static final String WHITESPACE_TOKENIZER 		= "WHITESPACE_TOKENIZER";
	public static final String SEPARATOR_TOKENIZER 			= "SEPARATOR_TOKENIZER";
	public static final String AGGRESSIVE_TOKENIZER 		= "AGGRESSIVE_TOKENIZER";
	
	// -------- Comparer factory constants --------
	// Note : String is the default type of token, so need to add token type context for strings
	public static final String EXACT_STRING_COMPARER 		= "EXACT";
	public static final String FUZZY_STRING_COMPARER 		= "FUZZY";
	public static final String EXACT_INTEGER_COMPARER 		= "EXACT_INTEGER";
	public static final String FUZZY_INTEGER_COMPARER 		= "FUZZY_INTEGER";
	public static final String EXACT_DOUBLE_COMPARER 		= "EXACT_DOUBLE";
	public static final String FUZZY_DOUBLE_COMPARER 		= "FUZZY_DOUBLE";
	public static final String EXACT_VALUE_COMPARER			= "EXACT_VALUE";

	public static final String STRING_COMPARER				= "STRING_COMPARER";
	public static final String INTEGER_COMPARER				= "INTEGER_COMPARER";
	public static final String DOUBLE_COMPARER				= "DOUBLE_COMPARER";
	
	// --------- Audit constants ------------------
	public static final String TOTAL_ITEMPAIRS 				= "TOTAL_ITEMPAIRS";
	public static final String MATCHED_ITEMPAIRS			= "MATCHED_ITEMPAIRS";
	public static final String MISMATCHED_ITEMPAIRS			= "MISMATCHED_ITEMPAIRS";
	
	public static final String PASSED_CLAUSES				= "PASSED_CLAUSES";
	public static final String FAILED_CLAUSES				= "FAILED_CLAUSES";
	
	// ---------- Rule post processing constants ---
	public static final String ALL_ITEMPAIR_ATTRIBUTES      = "ALL_ITEMPAIR_ATTRIBUTES";
	
	// ---------- Itempair data file constants ----------
	public static final String ID 							= "ID";
	public static final String SOURCE 						= "Source";
	public static final String TITLE 						= "Title";
	public static final String PD_TITLE 					= "pd_title";
	public static final String PD_TITLE_WITHOUT_NUMBER_UNITS_AND_VARIATIONS = "pd_title_without_number_units_and_variations";
	public static final String SIGNING_DESCRIPTION 		= "signing_desc"; 
	public static final String NULL_STRING					= "null";
	
	public static final String AND_WEB_FORMAT				= "&amp;";
	public static final String LESS_THAN_WEB_FORMAT			= "&lt;";
	public static final String GREATER_THAN_WEB_FORMAT		= "&gt;";
	public static final String QUOTE_WEB_FORMAT				= "&quot;";
	
	// ---------- Delimiters -----------------------------
	public static final String COMMA						= ",";
	public static final String COLUMN_DELIMITER				= "\\|#";
	public static final String VALUE_DELIMITER				= ",";
	public static final String SEPARATOR_DELIMITER			= ";#";
	
	// ---------- Special Strings ------------------------
	public static final String NULL_AS_STRING				= "null";
	
	// ---------- File location constants ----------------
	public static final String DATA_FILE_PATH_PREFIX		= System.getProperty("user.dir") + "/src/main/resources/data/";
	public static final String RULE_FILE_PATH_PREFIX		= System.getProperty("user.dir") + "/src/main/resources/rules/";

	// Expanded rule files - Readable
	public static final String WSE_CNET_RULE_FILE_PATH = RULE_FILE_PATH_PREFIX + "item-matching-rules-cnet-wse-baseline-expanded.txt";
	public static final String WSE_BOWKER_RULE_FILE_PATH = RULE_FILE_PATH_PREFIX + "item-matching-rules-bowker-wse-baseline-expanded.txt";	
	public static final String WSE_CNET_BOOK_RULE_FILE_PATH = RULE_FILE_PATH_PREFIX + "wse_cnet_book_matching_rules_expanded.rl";
	
	// Condensed rule files - Uses advanced features of rule language to reduce the size of rule file
	public static final String WSE_CNET_CONDENSED_RULE_FILE_PATH = RULE_FILE_PATH_PREFIX + "wse_cnet_matching_rules.rl";
	public static final String WSE_CNET_BOOK_CONDENSED_RULE_FILE_PATH = RULE_FILE_PATH_PREFIX + "wse_cnet_book_matching_rules.rl";	
	public static final String WSE_BOWKER_CONDENSED_RULE_FILE_PATH = RULE_FILE_PATH_PREFIX + "wse_bowker_matching_rules.rl";
	public static final String WSE_STORES_CONDENSED_RULE_FILE_PATH = RULE_FILE_PATH_PREFIX + "wse_stores_matching_rules.rl";
}
