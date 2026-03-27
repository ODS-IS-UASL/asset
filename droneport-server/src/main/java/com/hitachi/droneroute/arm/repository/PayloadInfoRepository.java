package com.hitachi.droneroute.arm.repository;

import com.hitachi.droneroute.arm.entity.PayloadInfoEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

/** ペイロード情報のリポジトリ */
public interface PayloadInfoRepository extends JpaRepository<PayloadInfoEntity, UUID> {

  /**
   * ペイロードIDによるペイロード情報の検索(削除済みレコードを除く)
   *
   * @param payloadId ペイロードID
   * @return ペイロード情報
   */
  Optional<PayloadInfoEntity> findByPayloadIdAndDeleteFlagFalse(UUID payloadId);

  /**
   * 機体IDによる機体に紐づくペイロード情報の最大ペイロード番号の取得
   *
   * @param aircraftId 機体ID
   * @return 最大ペイロード番号
   */
  @Query(
      "SELECT MAX(p.payloadNumber) FROM PayloadInfoEntity p WHERE p.aircraftId = :aircraftId AND p.deleteFlag = false")
  Optional<Integer> findMaxPayloadNumberByAircraftIdAndDeleteFlagFalse(
      @Param("aircraftId") UUID aircraftId);

  /**
   * 機体IDによる機体に紐づくペイロード情報の検索(削除済みレコードを除く)
   *
   * @param aircraftId 機体ID
   * @return ペイロード情報
   */
  List<PayloadInfoEntity> findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(
      @Param("aircraftId") UUID aircraftId);

  /**
   * 機体IDによる機体に紐づくペイロード情報の論理削除
   *
   * @param aircraftId 機体ID
   * @param operatorId
   * @return 更新件数
   */
  @Modifying
  @Query(
      "UPDATE PayloadInfoEntity p SET p.deleteFlag = true, p.updateTime = CURRENT_TIMESTAMP, operatorId=:operatorId WHERE p.aircraftId = :aircraftId")
  int deleteByAircraftId(
      @Param("aircraftId") UUID aircraftId, @Param("operatorId") String operatorId);

  /**
   * 検索条件による機体情報一覧取得
   *
   * @param aircraftIds 機体IDリスト
   * @return　機体情報リスト
   */
  List<PayloadInfoEntity> findAllByAircraftIdInAndDeleteFlagFalse(
      @Nullable Iterable<UUID> aircraftIds);

  /**
   * 機体IDによる機体に紐づくペイロード数取得
   *
   * @param aircraftId 機体ID
   * @return 登録済み件数
   */
  int countByAircraftIdAndDeleteFlagFalse(@Param("aircraftId") UUID aircraftId);
}
