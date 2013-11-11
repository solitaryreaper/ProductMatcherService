package com.walmart.productgenome.pairComparison.utils.evaluators;

import java.util.HashMap;
import java.util.Map;

import com.walmart.productgenome.pairComparison.model.Constants;

public class ClauseEvaluatorFactory {

	private static final IAttributeMatchClauseEvaluator TWO_WAY_CLAUSE_EVALUATOR = new TwoWayClauseEvaluator();
	private static final IAttributeMatchClauseEvaluator ONE_WAY_CLAUSE_EVALUATOR = new OneWayClauseEvaluator();
	
	private static final Map<String, IAttributeMatchClauseEvaluator> EVALUATOR_MAP = new HashMap<String, IAttributeMatchClauseEvaluator>();
	static {
		EVALUATOR_MAP.put(Constants.ONE_WAY_EVALUATOR, ONE_WAY_CLAUSE_EVALUATOR);
		EVALUATOR_MAP.put(Constants.TWO_WAY_EVALUATOR , TWO_WAY_CLAUSE_EVALUATOR);
	}
	
	public static IAttributeMatchClauseEvaluator getEvaluator(final String evaluatorType) {
		if (EVALUATOR_MAP.containsKey(evaluatorType) == false)
			throw new IllegalArgumentException("evaluator type cannot be null");
		
		return EVALUATOR_MAP.get(evaluatorType);
	}
	
	public static String getEvaluatorType(IAttributeMatchClauseEvaluator evaluator)
	{
		String evaluatorType = null;
		for(Map.Entry<String, IAttributeMatchClauseEvaluator> entry : EVALUATOR_MAP.entrySet()) {
			IAttributeMatchClauseEvaluator currEvaluator = entry.getValue();
			if(currEvaluator.getClass().equals(evaluator.getClass())) {
				evaluatorType = entry.getKey();
			}
		}
		return evaluatorType;
	}
}
