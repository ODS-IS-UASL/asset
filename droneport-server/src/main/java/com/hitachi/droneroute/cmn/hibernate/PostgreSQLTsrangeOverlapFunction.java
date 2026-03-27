package com.hitachi.droneroute.cmn.hibernate;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

/** Specificationクラスで使用する関数を定義するクラス */
public class PostgreSQLTsrangeOverlapFunction implements FunctionContributor {

  /**
   * tsrangeの演算子を使用する関数を定義する fn_tsrange_overlap: tsrangeの重なりを判定する関数 fn_tsrange_include:
   * tsrangeが範囲を包含するか判定する関数 fn_tsrange_include2: tsrangeが範囲を包含されるか判定する関数 tsrange_lower:
   * tsrangeの下限値を取得する関数 tsrange_upper: tsrangeの上限値を取得する関数
   *
   * @param functionContributions 関数の定義を行うためのオブジェクト
   */
  @Override
  public void contributeFunctions(FunctionContributions functionContributions) {
    functionContributions
        .getFunctionRegistry()
        .registerPattern(
            "fn_tsrange_overlap",
            "?1 && ?2::tsrange",
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.BOOLEAN));

    functionContributions
        .getFunctionRegistry()
        .registerPattern(
            "fn_tsrange_include",
            "?1 @> ?2::tsrange",
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.BOOLEAN));

    functionContributions
        .getFunctionRegistry()
        .registerPattern(
            "fn_tsrange_include2",
            "?1::tsrange @> ?2",
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.BOOLEAN));

    functionContributions
        .getFunctionRegistry()
        .registerPattern(
            "tsrange_lower",
            "lower(?1)",
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.TIMESTAMP));

    functionContributions
        .getFunctionRegistry()
        .registerPattern(
            "tsrange_upper",
            "upper(?1)",
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.TIMESTAMP));
  }
}
