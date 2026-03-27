package com.hitachi.droneroute.arm.repository;

import com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementRes;
import com.hitachi.droneroute.arm.entity.FileInfoEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

/** 補足資料情報のリポジトリ */
public interface FileInfoRepository extends JpaRepository<FileInfoEntity, UUID> {

  /**
   * 補足資料IDによる補足情報ファイルの検索(削除済みレコードを除く)
   *
   * @param fileId 補足資料ID
   * @return 補足情報ファイル情報
   */
  Optional<FileInfoEntity> findByFileIdAndDeleteFlagFalse(UUID fileId);

  /**
   * 機体IDによる機体に紐づく補足情報ファイルの最大ファイル番号の取得
   *
   * @param aircraftId 機体ID
   * @return 最大ファイル番号
   */
  @Query(
      "SELECT MAX(f.fileNumber) FROM FileInfoEntity f WHERE f.aircraftId = :aircraftId AND f.deleteFlag = false")
  Optional<Integer> findMaxFileNumberByAircraftIdAndDeleteFlagFalse(
      @Param("aircraftId") UUID aircraftId);

  /**
   * 機体IDによる機体に紐づく補足情報ファイルの検索(ファイルデータは含まない/削除済みレコードを除く)
   *
   * @param aircraftId 機体ID
   * @return 補足情報ファイル情報(詳細画面用DTO)
   */
  @NonNull
  @Query(
      "SELECT "
          + "new com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementRes("
          + "f.fileLogicalName, "
          + "f.filePhysicalName, "
          + "CAST(f.fileId AS string)"
          + ") "
          + "FROM FileInfoEntity f "
          + "WHERE f.aircraftId = :aircraftId "
          + "AND f.deleteFlag = false "
          + "ORDER BY f.fileNumber ASC")
  List<AircraftInfoFileInfoListElementRes> findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(
      @Param("aircraftId") UUID aircraftId);

  /**
   * 機体IDによる機体に紐づく補足情報ファイルの論理削除
   *
   * @param aircraftId 機体ID
   * @param operatorId オペレータID
   * @return 更新件数
   */
  @Modifying
  @Query(
      "UPDATE FileInfoEntity f SET f.deleteFlag = true, f.updateTime = CURRENT_TIMESTAMP, f.operatorId=:operatorId WHERE f.aircraftId = :aircraftId")
  int deleteByAircraftId(
      @Param("aircraftId") UUID aircraftId, @Param("operatorId") String operatorId);

  /**
   * 機体IDによる機体に紐づく補足情報ファイル数取得
   *
   * @param aircraftId 機体ID
   * @return 登録済み件数
   */
  int countByAircraftIdAndDeleteFlagFalse(@Param("aircraftId") UUID aircraftId);
}
