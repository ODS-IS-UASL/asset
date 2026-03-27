package com.hitachi.droneroute.arm.validator;

import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoRequestDto;
import com.hitachi.droneroute.cmn.validator.Validator;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** 機体予約情報関連APIパラメータチェッククラス */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AircraftReserveInfoValidator extends Validator {

  /**
   * 機体予約情報登録APIパラメータチェック
   *
   * @param dto 機体予約情報登録APIのリクエストDTO
   */
  public void validateForRegist(AircraftReserveInfoRequestDto dto) {
    // 登録時必須チェック
    validateForRegistRequied(dto);

    // 入力チェック
    validateForRegistUpdate(dto);

    validate();
  }

  /**
   * 機体予約情報更新APIパラメータチェック
   *
   * @param dto 機体予約情報更新APIのリクエストDTO
   */
  public void validateForUpdate(AircraftReserveInfoRequestDto dto) {
    // 更新時必須チェック
    validateForUpdateRequired(dto);

    // 入力チェック
    validateForRegistUpdate(dto);

    validate();
  }

  /**
   * 機体予約情報一覧APIパラメータチェック
   *
   * @param dto 機体予約情報一覧APIのリクエストDTO
   */
  public void validateForGetList(AircraftReserveInfoListRequestDto dto) {
    // 入力チェック
    validateForReserveList(dto);

    validate();
  }

  /**
   * 機体予約情報詳細APIパラメータチェック
   *
   * @param aircraftReserveId 機体予約ID
   */
  public void validateForDetail(String aircraftReserveId) {
    // 入力チェック
    notNull("機体予約ID", aircraftReserveId);
    checkUUID("機体予約ID", aircraftReserveId);

    validate();
  }

  /**
   * 登録時必須チェック
   *
   * @param dto 機体予約情報登録APIのリクエストDTO
   */
  private void validateForRegistRequied(AircraftReserveInfoRequestDto dto) {
    // 一括予約ID
    notNull("一括予約ID", dto.getGroupReservationId());

    // 機体ID
    notNull("機体ID", dto.getAircraftId());

    // 予約開始時間
    notNull("予約開始時間", dto.getReservationTimeFrom());

    // 予約終了時間
    notNull("予約終了時間", dto.getReservationTimeTo());
  }

  /**
   * 登録更新時入力チェック
   *
   * @param dto 機体予約情報登録APIのリクエストDTO
   */
  private void validateForRegistUpdate(AircraftReserveInfoRequestDto dto) {
    // 機体予約ID
    checkUUID("機体予約ID", dto.getAircraftReservationId());

    // 一括予約ID
    checkUUID("一括予約ID", dto.getGroupReservationId());

    // 機体ID
    checkUUID("機体ID", dto.getAircraftId());

    // 予約開始時間
    checkDateTime("予約開始時間", dto.getReservationTimeFrom());

    // 予約終了時間
    checkDateTime("予約終了時間", dto.getReservationTimeTo());
  }

  /**
   * 更新時必須チェック
   *
   * @param dto 機体予約情報更新APIのリクエストDTO
   */
  private void validateForUpdateRequired(AircraftReserveInfoRequestDto dto) {
    // 機体予約ID
    notNull("機体予約ID", dto.getAircraftReservationId());
  }

  /**
   * 機体予約一覧入力チェック
   *
   * @param dto 機体予約情報一覧APIのリクエストDTO
   */
  private void validateForReserveList(AircraftReserveInfoListRequestDto dto) {
    // 一括予約ID
    checkUUID("一括予約ID", dto.getGroupReservationId());

    // 機体ID
    checkUUID("機体ID", dto.getAircraftId());

    // 機体名 24文字
    checkLength("機体名", dto.getAircraftName(), 24);

    // 日時条件（開始）
    checkDateTime("日時条件（開始）", dto.getTimeFrom());

    // 日時条件（終了）
    checkDateTime("日時条件（終了）", dto.getTimeFrom());

    // 予約事業者ID
    checkUUID("予約事業者ID", dto.getReserveProviderId());

    // ソート順
    // カンマ区切りで0、または1のみ
    checkRange("ソート順", dto.getSortOrders(), new Integer[] {0, 1});

    // ソート順の設定数と、ソート対象列の設定数
    {
      int sortOrdersNum = 0;
      if (StringUtils.hasText(dto.getSortOrders())) {
        sortOrdersNum = dto.getSortOrders().split(",").length;
      }
      int sortColumnsNum = 0;
      if (StringUtils.hasText(dto.getSortColumns())) {
        sortColumnsNum = dto.getSortColumns().split(",").length;
      }
      if (sortOrdersNum != sortColumnsNum) {
        details.add(errorMessage_sort);
      }
    }

    // ページ制御項目のチェック
    checkRangeInteger("1ページ当たりの件数", dto.getPerPage(), 1, 100);
    checkRangeInteger("現在ページ番号", dto.getPage(), 1, Integer.MAX_VALUE);
    if (!StringUtils.hasText(dto.getPage()) && StringUtils.hasText(dto.getPerPage())) {
      notNull("現在ページ番号", dto.getPage());
    }
    if (StringUtils.hasText(dto.getPage()) && !StringUtils.hasText(dto.getPerPage())) {
      notNull("1ページ当たりの件数", dto.getPerPage());
    }
  }
}
