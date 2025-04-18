package com.hitachi.droneroute.cmn.hibernate;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

// 以下を参考にして実装
// https://stackoverflow.com/questions/76970467/construct-a-criteria-api-predicate-that-overlaps-a-tsrange-column-type-in-postgr
// https://qiita.com/InoueRah/items/ca2c207d4a57799d4f71

/**
 * Specificationクラスで使用する関数を定義するクラス
 * @author dpls01
 *
 */
public class PostgreSQLTsrangeOverlapFunction implements FunctionContributor {

	/**
	 * tsrangeの演算子を使用する関数を定義する
	 */
	@Override
	public void contributeFunctions(FunctionContributions functionContributions) {
		functionContributions.getFunctionRegistry().registerPattern(
				"fn_tsrange_overlap", 
				"?1 && ?2::tsrange", 
				functionContributions
					.getTypeConfiguration()
					.getBasicTypeRegistry()
					.resolve(StandardBasicTypes.BOOLEAN)
		);
		
		functionContributions.getFunctionRegistry().registerPattern(
				"fn_tsrange_include", 
				"?1 @> ?2::tsrange", 
				functionContributions
					.getTypeConfiguration()
					.getBasicTypeRegistry()
					.resolve(StandardBasicTypes.BOOLEAN)
		);
		
		functionContributions.getFunctionRegistry().registerPattern(
				"fn_tsrange_include2", 
				"?1::tsrange @> ?2", 
				functionContributions
					.getTypeConfiguration()
					.getBasicTypeRegistry()
					.resolve(StandardBasicTypes.BOOLEAN)
		);
		
		functionContributions.getFunctionRegistry().registerPattern(
				"tsrange_lower", 
				"lower(?1)", 
				functionContributions
					.getTypeConfiguration()
					.getBasicTypeRegistry()
					.resolve(StandardBasicTypes.TIMESTAMP)
		);
		
		functionContributions.getFunctionRegistry().registerPattern(
				"tsrange_upper", 
				"upper(?1)", 
				functionContributions
					.getTypeConfiguration()
					.getBasicTypeRegistry()
					.resolve(StandardBasicTypes.TIMESTAMP)
		);
	}

}
