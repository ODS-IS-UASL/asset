package com.hitachi.droneroute.prm.repository;

import com.hitachi.droneroute.prm.entity.PriceHistoryInfoEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** 料金履歴情報のリポジトリ */
public interface PriceHistoryInfoRepository
    extends JpaRepository<PriceHistoryInfoEntity, UUID>,
        JpaSpecificationExecutor<PriceHistoryInfoEntity> {

  /**
   * 料金IDによる料金履歴情報の検索(削除済みレコードを除く)
   *
   * @param priceId 料金ID
   * @return 料金履歴情報
   */
  Optional<PriceHistoryInfoEntity> findByPriceIdAndDeleteFlagFalse(UUID priceId);
}
