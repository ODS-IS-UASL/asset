package com.hitachi.droneroute.cmn.service;

import com.hitachi.droneroute.cmn.entity.CommonEntity;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.StringUtils;

/** 離着陸場管理/機体管理共通処理クラス */
public interface DroneRouteCommonService {

  /**
   * ページ制御オブジェクトを生成する
   *
   * @param strPerPage 1ページ当たりの件数(String型)
   * @param strPage ページ番号(String型)
   * @param sort ソート条件
   * @return ページ制御オブジェクト
   */
  default Pageable createPageRequest(String strPerPage, String strPage, Sort sort) {
    Pageable result = null;
    Integer page = StringUtils.hasText(strPage) ? Integer.valueOf(strPage) : null;
    Integer perPage = StringUtils.hasText(strPerPage) ? Integer.valueOf(strPerPage) : null;
    if (Objects.nonNull(page) && page > 0) {
      result = PageRequest.of(page - 1, perPage, Objects.isNull(sort) ? Sort.unsorted() : sort);
    }
    return result;
  }

  /**
   * ソート条件オブジェクトを生成する
   *
   * @param sortOrders ソート順(0:昇順,1:降順)をカンマ区切りで設定(ソート対象列名の設定順に対応させる)
   * @param sortColumns ソート対象列名をカンマ区切りで設定
   * @param logger ロガー
   * @return ソート条件オブジェクト
   */
  default Sort createSort(String sortOrders, String sortColumns, Logger logger) {
    Sort result = null;
    String[] arrSortOrders = new String[] {};
    String[] arrSortColumns = new String[] {};

    if (org.springframework.util.StringUtils.hasText(sortOrders)) {
      arrSortOrders = sortOrders.split(",");
    }
    if (org.springframework.util.StringUtils.hasText(sortColumns)) {
      arrSortColumns = sortColumns.split(",");
    }

    // ソート順とソート対象列の設定数が一致していない場合は、ソート条件なしで処理継続する
    if (arrSortOrders.length == arrSortColumns.length) {
      if (arrSortOrders.length > 0) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < arrSortColumns.length; ++i) {
          if (arrSortOrders[i].equals("0")) {
            orders.add(Order.asc(arrSortColumns[i]));
          } else {
            orders.add(Order.desc(arrSortColumns[i]));
          }
        }
        result = Sort.by(orders);
      }
    } else {
      logger.warn("ソート順とソート対象列の設定数が不一致のため、ソート条件を無効にします。");
    }
    return result;
  }

  /**
   * 更新の認可をチェックする
   *
   * @param entity 更新対象のエンティティ
   * @param operatorId オペレータID
   */
  default void checkUpdatePermissionByOperatorId(CommonEntity entity, String operatorId) {
    if (!StringUtils.hasText(entity.getOperatorId())) {
      throw new ServiceErrorException("処理対象のデータにオペレータIDに値が設定されていません。");
    } else if (!entity.getOperatorId().equals(operatorId)) {
      throw new ServiceErrorException("認可エラー。オペレータIDが一致しません。");
    }
  }

  /**
   * 運航事業者での更新/削除の認可をチェックする
   *
   * @param reserveProviderId 更新/削除対象のエンティティの予約事業者ID
   * @param affiliatedOperatorId オペレータID(所属事業者)
   */
  default void checkUpdatePermissionByAffiliatedOperatorId(
      UUID reserveProviderId, String affiliatedOperatorId) {
    if (reserveProviderId == null
        || affiliatedOperatorId == null
        || !reserveProviderId.equals(UUID.fromString(affiliatedOperatorId))) {
      throw new ServiceErrorException("認可エラー。予約事業者IDが一致しません。");
    }
  }
}
