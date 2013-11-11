package com.walmart.productgenome.pairComparison.utils.data.tools;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.walmart.productgenome.pairComparison.model.Constants;

/**
 * 
 * @author sprasa4
 *
 */
public class BulkItempairMatchAnalyzerTest {

	@Ignore
	public void testBulkItempairMatchAnalysis()
	{
		String dataDir = "/Users/sprasa4/Data/Work/resources/data/wse_cnet_mismatch/old_itempair_split";
		File ruleFile = new File(Constants.WSE_CNET_RULE_FILE_PATH);
		
		ExcelUtils analysisSheet = new ExcelUtils("wse_cnet_new_old_mismatch", "/Users/sprasa4/Desktop");
		analysisSheet.writeRowToExcelFile(Lists.newArrayList("source", "target", "match", "rule", "subrule", "expected", "actual"));
		File[] folder = new File(dataDir).listFiles();
		for(File dataFile : folder) {
			System.out.println(dataFile.getAbsolutePath());
			List<List<String>> itempairsAnalysis = BulkItempairMatchAnalyzer.analyzeItemPairFile(dataFile, ruleFile);
			for(List<String> row : itempairsAnalysis) {
				analysisSheet.writeRowToExcelFile(row);
			}
  		}
		
		analysisSheet.saveExcelFile();
	}
}
