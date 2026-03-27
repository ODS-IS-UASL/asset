package com.hitachi.droneroute.arm.repository;

import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/** 機体情報のリポジトリ */
public interface AircraftInfoRepository
    extends JpaRepository<AircraftInfoEntity, UUID>, JpaSpecificationExecutor<AircraftInfoEntity> {

  /**
   * 機体IDによる機体情報の検索(削除済みレコードを除く)
   *
   * @param aircraftId 機体ID
   * @return 機体情報
   */
  Optional<AircraftInfoEntity> findByAircraftIdAndDeleteFlagFalse(UUID aircraftId);

  /**
   * 機体IDによる機体情報の検索(削除済みまたは非公開設定レコードを除く)
   *
   * @param aircraftId 機体ID
   * @return 機体情報
   */
  Optional<AircraftInfoEntity> findByAircraftIdAndDeleteFlagFalseAndPublicFlagTrue(UUID aircraftId);

  /**
   * 検索条件による機体情報一覧取得
   *
   * @param spec　検索条件
   * @return　機体情報リスト
   */
  @NonNull
  List<AircraftInfoEntity> findAll(@Nullable Specification<AircraftInfoEntity> spec);

  /**
   * 検索条件による機体情報一覧取得
   *
   * @param spec　検索条件
   * @param sort　ソート条件
   * @return　機体情報リスト
   */
  @NonNull
  List<AircraftInfoEntity> findAll(
      @Nullable Specification<AircraftInfoEntity> spec, @Nullable Sort sort);

  /**
   * 検索条件による機体情報一覧取得
   *
   * @param spec　検索条件
   * @param pageable ページ条件
   * @return ページ情報(検索結果含む)
   */
  @NonNull
  Page<AircraftInfoEntity> findAll(
      @Nullable Specification<AircraftInfoEntity> spec, @Nullable Pageable pageable);
}
