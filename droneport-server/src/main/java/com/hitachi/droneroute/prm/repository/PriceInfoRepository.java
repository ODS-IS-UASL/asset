package com.hitachi.droneroute.prm.repository;

import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
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

/** 料金情報のリポジトリ */
public interface PriceInfoRepository
    extends JpaRepository<PriceInfoEntity, UUID>, JpaSpecificationExecutor<PriceInfoEntity> {

  /**
   * 料金IDによる料金情報の検索(削除済みレコードを除く)
   *
   * @param priceId
   * @return 料金情報
   */
  Optional<PriceInfoEntity> findByPriceIdAndDeleteFlagFalse(UUID priceId);

  /**
   * 検索条件による料金情報一覧取得
   *
   * @param spec　検索条件
   * @return　料金情報リスト
   */
  @NonNull
  List<PriceInfoEntity> findAll(@Nullable Specification<PriceInfoEntity> spec);

  /**
   * 検索条件による料金情報一覧取得
   *
   * @param spec　検索条件
   * @param sort　ソート条件
   * @return　料金情報リスト
   */
  @NonNull
  List<PriceInfoEntity> findAll(@Nullable Specification<PriceInfoEntity> spec, @Nullable Sort sort);

  /**
   * 検索条件による料金情報一覧取得
   *
   * @param spec　検索条件
   * @param pageable ページ条件
   * @return ページ情報(検索結果含む)
   */
  @NonNull
  Page<PriceInfoEntity> findAll(
      @Nullable Specification<PriceInfoEntity> spec, @Nullable Pageable pageable);
}
