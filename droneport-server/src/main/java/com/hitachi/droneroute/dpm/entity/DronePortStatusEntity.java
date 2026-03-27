package com.hitachi.droneroute.dpm.entity;

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

/** 離着陸場状態エンティティクラス */
@Setter
@Getter
@Table(name = "droneport_status")
@Entity
public class DronePortStatusEntity extends CommonEntity {
  /** 離着陸場ID */
  @Id
  @Column(name = "droneport_id")
  private String dronePortId;

  /** 動作状況(使用可) */
  @Column(name = "active_status")
  private Integer activeStatus;

  /** 動作状況(使用不可) */
  @Column(name = "inactive_status")
  private Integer inactiveStatus;

  /** 使用不可日時範囲 */
  @Type(PostgreSQLRangeType.class)
  @Column(name = "inactive_time", columnDefinition = "tsrange")
  private Range<LocalDateTime> inactiveTime;

  /** 格納中機体ID */
  @Column(name = "stored_aircraft_id")
  private UUID storedAircraftId;
}
