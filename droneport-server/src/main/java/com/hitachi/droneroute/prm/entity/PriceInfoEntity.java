package com.hitachi.droneroute.prm.entity;

import com.hitachi.droneroute.cmn.entity.CommonEntity;
import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

/** 料金情報Entity */
@Setter
@Getter
@Entity
@Table(name = "price_info")
public class PriceInfoEntity extends CommonEntity {
  /** 料金ID */
  @Id
  @Column(name = "price_id")
  private UUID priceId;

  /** リソースID */
  @Column(name = "resource_id")
  private String resourceId;

  /** リソース種別 */
  @Column(name = "resource_type")
  private Integer resourceType;

  /** 主管航路事業者ID */
  @Column(name = "primary_route_operator_id")
  private String primaryRouteOperatorId;

  /** 料金タイプ */
  @Column(name = "price_type")
  private Integer priceType;

  /** 料金単位 */
  @Column(name = "price_per_unit")
  private Integer pricePerUnit;

  /** 料金 */
  @Column(name = "price")
  private Integer price;

  /** 適用日時範囲 */
  @Type(PostgreSQLRangeType.class)
  @Column(name = "effective_time", columnDefinition = "tsrange")
  private Range<LocalDateTime> effectiveTime;

  /** 優先度 */
  @Column(name = "priority")
  private Integer priority;
}
