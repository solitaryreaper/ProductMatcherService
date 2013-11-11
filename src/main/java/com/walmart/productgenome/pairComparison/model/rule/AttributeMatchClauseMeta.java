package com.walmart.productgenome.pairComparison.model.rule;

import java.util.List;
import java.util.Map;

import com.walmart.productgenome.pairComparison.model.Constants;
import com.walmart.productgenome.pairComparison.utils.comparers.ComparersFactory;
import com.walmart.productgenome.pairComparison.utils.comparers.IComparer;
import com.walmart.productgenome.pairComparison.utils.evaluators.ClauseEvaluatorFactory;
import com.walmart.productgenome.pairComparison.utils.evaluators.IAttributeMatchClauseEvaluator;
import com.walmart.productgenome.pairComparison.utils.tokenizers.IStringTokenizer;
import com.walmart.productgenome.pairComparison.utils.tokenizers.TokenizerFactory;

/**
 * Collection of metadata constructs that guide the product matching process.
 * 
 * @author sprasa4
 *
 */
public class AttributeMatchClauseMeta {

	/** the string tokenizer to be used for source */
	private final IStringTokenizer m_sourceTokenizer;

	/** the string tokenizer to be used for destination */
	private final IStringTokenizer m_targetTokenizer;
	
	/** the string comparers to be used */
	private final List<IComparer> m_comparers;
	
	/** A boolean flag indicating whether it's okay for the source attribute to be missing.
	 * TRUE  would mean that the attribute match clause will evaluate to TRUE  if the source attribute is missing 
	 * FALSE would mean that the attribute match clause will evaluate to FALSE if the source attribute is missing 
	 * (this flag is only used if the source attribute is missing) */
	private final boolean m_missingOkay;
	
	/** the evaluator to be used */
	private final IAttributeMatchClauseEvaluator m_evaluator;
	
	/** the score threshold */
	private final double m_scoreThreshold;

	/**
	 * @param sourceItemAttributes the source item attributes [NOT NULL]
	 * @param targetItemAttributes the list of target item attributes [NOT NULL]
	 * @param tokenizer the tokenizer to be used [NOT NULL]
	 * @param comparers the comparers to be used [NOT NULL]
	 * @param missingOkay the boolean that specifies whether it's okay for the source attribute to be missing or not [NOT NULL]
	 * @param clauseEvaluator the evaluator to be used [NOT NULL]
	 */
	public AttributeMatchClauseMeta(final IStringTokenizer sourceTokenizer, final IStringTokenizer targetTokenizer,
									final List<IComparer> comparers, final boolean missingOkay, 
									final IAttributeMatchClauseEvaluator clauseEvaluator, final double scoreThreshold) 
	{
		if (sourceTokenizer == null) throw new IllegalArgumentException("source tokenizer cannot be null");
		if (comparers == null) throw new IllegalArgumentException("comparers cannot be null");
		if (clauseEvaluator == null) throw new IllegalArgumentException("clause evaluator cannot be null");
		
		m_sourceTokenizer = sourceTokenizer;
		if(targetTokenizer == null) {
			m_targetTokenizer = sourceTokenizer;
		}
		else {
			m_targetTokenizer = targetTokenizer;
		}
		m_comparers = comparers;
		m_missingOkay = missingOkay;
		m_evaluator = clauseEvaluator;
		m_scoreThreshold = scoreThreshold;
	}

	/**
	 * Constructs a attributeMatchClauseMeta object from a key-value pair description of the meta
	 * information.
	 */
	public AttributeMatchClauseMeta(Map<String, String> clauseMetaMap)
	{
		// Do the mandatory attribute checks here
		if(!clauseMetaMap.containsKey(Constants.SOURCE_TOKENIZER)) {
			throw new IllegalArgumentException("source tokenizer cannot be null");
		}
		if(!clauseMetaMap.containsKey(Constants.ATTRIBUTE_COMPARER)) {
			throw new IllegalArgumentException("comparer cannot be null");
		}
		if(!clauseMetaMap.containsKey(Constants.CLAUSE_EVALUATOR)) {
			throw new IllegalArgumentException("evaluator cannot be null");
		}
				
		m_sourceTokenizer = TokenizerFactory.getTokenizer(clauseMetaMap.get(Constants.SOURCE_TOKENIZER));
		if(!clauseMetaMap.containsKey(Constants.TARGET_TOKENIZER)) {
			m_targetTokenizer = m_sourceTokenizer;
		}
		else {
			m_targetTokenizer = TokenizerFactory.getTokenizer(clauseMetaMap.get(Constants.TARGET_TOKENIZER));
		}
		
		m_comparers = ComparersFactory.getComparers(clauseMetaMap.get(Constants.ATTRIBUTE_COMPARER));
		m_missingOkay = Boolean.valueOf(clauseMetaMap.get(Constants.MISSING_ATTRIBUTE_ALLOWED));
		m_evaluator = ClauseEvaluatorFactory.getEvaluator(clauseMetaMap.get(Constants.CLAUSE_EVALUATOR));
		m_scoreThreshold = Double.parseDouble(clauseMetaMap.get(Constants.SCORE_THRESHOLD));		
	}
	
	public IStringTokenizer getSourceTokenizer() {
		return m_sourceTokenizer;
	}

	public IStringTokenizer getTargetTokenizer() {
		return m_targetTokenizer;
	}

	public List<IComparer> getComparers() {
		return m_comparers;
	}

	public boolean isMissingOkay() {
		return m_missingOkay;
	}

	public IAttributeMatchClauseEvaluator getEvaluator() {
		return m_evaluator;
	}

	public double getScoreThreshold() {
		return m_scoreThreshold;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AttributeMatchClauseMeta [m_sourceTokenizer=")
				.append(m_sourceTokenizer).append(", m_targetTokenizer=")
				.append(m_targetTokenizer).append(", m_comparers=")
				.append(m_comparers).append(", m_missingOkay=")
				.append(m_missingOkay).append(", m_evaluator=")
				.append(m_evaluator).append(", m_scoreThreshold=")
				.append(m_scoreThreshold).append("]");
		return builder.toString();
	}
	
}
