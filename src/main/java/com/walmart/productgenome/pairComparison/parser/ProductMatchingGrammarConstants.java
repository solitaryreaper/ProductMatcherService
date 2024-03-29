/* Generated By:JavaCC: Do not edit this line. ProductMatchingGrammarConstants.java */
package com.walmart.productgenome.pairComparison.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ProductMatchingGrammarConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int SINGLE_LINE_COMMENT = 8;
  /** RegularExpression Id. */
  int FORMAL_COMMENT = 9;
  /** RegularExpression Id. */
  int MULTI_LINE_COMMENT = 10;
  /** RegularExpression Id. */
  int SEMICOLON = 12;
  /** RegularExpression Id. */
  int COMMA = 13;
  /** RegularExpression Id. */
  int EQUALS = 14;
  /** RegularExpression Id. */
  int OPENBRACKET = 15;
  /** RegularExpression Id. */
  int CLOSEBRACKET = 16;
  /** RegularExpression Id. */
  int CREATE = 17;
  /** RegularExpression Id. */
  int INCLUDE = 18;
  /** RegularExpression Id. */
  int VARIABLE = 19;
  /** RegularExpression Id. */
  int AS = 20;
  /** RegularExpression Id. */
  int END = 21;
  /** RegularExpression Id. */
  int SUBRULE = 22;
  /** RegularExpression Id. */
  int USING = 23;
  /** RegularExpression Id. */
  int AND = 24;
  /** RegularExpression Id. */
  int OR = 25;
  /** RegularExpression Id. */
  int IN = 26;
  /** RegularExpression Id. */
  int VARIABLE_IDENTIFIER = 27;
  /** RegularExpression Id. */
  int RULE = 28;
  /** RegularExpression Id. */
  int RULESET = 29;
  /** RegularExpression Id. */
  int RULESET_ATTRIBUTES = 30;
  /** RegularExpression Id. */
  int COMPARER = 31;
  /** RegularExpression Id. */
  int SOURCE_TOKENIZER = 32;
  /** RegularExpression Id. */
  int TARGET_TOKENIZER = 33;
  /** RegularExpression Id. */
  int CONTAINMENT_EVALUATOR = 34;
  /** RegularExpression Id. */
  int MISSING_ATTRIBUTE_ALLOWED = 35;
  /** RegularExpression Id. */
  int SCORE_THRESHOLD = 36;
  /** RegularExpression Id. */
  int CLAUSE_ATTR_MATCH = 37;
  /** RegularExpression Id. */
  int BIDIRECTIONAL_MATCH = 38;
  /** RegularExpression Id. */
  int UNIDIRECTIONAL_MATCH = 39;
  /** RegularExpression Id. */
  int STRING_LITERAL = 40;
  /** RegularExpression Id. */
  int DECIMAL_FLOATING_POINT_LITERAL = 41;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int IN_SINGLE_LINE_COMMENT = 1;
  /** Lexical state. */
  int IN_FORMAL_COMMENT = 2;
  /** Lexical state. */
  int IN_MULTI_LINE_COMMENT = 3;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\r\"",
    "\"\\t\"",
    "\"\\n\"",
    "\"//\"",
    "<token of kind 6>",
    "\"/*\"",
    "<SINGLE_LINE_COMMENT>",
    "\"*/\"",
    "\"*/\"",
    "<token of kind 11>",
    "\";\"",
    "\",\"",
    "\"=\"",
    "<OPENBRACKET>",
    "<CLOSEBRACKET>",
    "\"CREATE\"",
    "<INCLUDE>",
    "\"VARIABLE\"",
    "\"AS\"",
    "\"END\"",
    "\"SUBRULE\"",
    "\"USING\"",
    "\"AND\"",
    "\"OR\"",
    "\"IN\"",
    "\"#\"",
    "\"RULE\"",
    "\"RULESET\"",
    "\"DEFAULT_RULESET_ATTRIBUTES\"",
    "\"COMPARER\"",
    "\"SOURCE_TOKENIZER\"",
    "\"TARGET_TOKENIZER\"",
    "\"EVALUATOR\"",
    "\"MISSING_ATTRIBUTE_ALLOWED\"",
    "\"SCORE\"",
    "\"MATCH\"",
    "\"BIDIRECTIONAL\"",
    "\"UNIDIRECTIONAL\"",
    "<STRING_LITERAL>",
    "<DECIMAL_FLOATING_POINT_LITERAL>",
  };

}
