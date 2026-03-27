package com.hitachi.droneroute.dpm.entity;

import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.cmn.entity.CommonEntity;
import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

/** 離着陸場予約情報エンティティクラス */
@Setter
@Getter
@Table(name = "droneport_reserve_info")
@Entity
@NamedEntityGraph(name = "droneport_reserve_info_join", includeAllAttributes = true)
public class DronePortReserveInfoEntity extends CommonEntity {

  /** 離着陸場予約ID */
  @Id
  @Column(name = "droneport_reservation_id")
  private UUID dronePortReservationId;

  /** 一括予約ID */
  @Column(name = "group_reservation_id")
  private UUID groupReservationId;

  /** 離着陸場ID */
  @Column(name = "droneport_id")
  private String dronePortId;

  /** 使用機体ID */
  @Column(name = "aircraft_id")
  private UUID aircraftId;

  /** 航路予約ID */
  @Column(name = "route_reservation_id")
  private UUID routeReservationId;

  /** 利用形態 */
  @Column(name = "usage_type")
  private Integer usageType;

  /** 予約日時範囲 */
  @Type(PostgreSQLRangeType.class)
  @Column(name = "reservation_time", columnDefinition = "tsrange")
  private Range<LocalDateTime> reservationTime;

  /** 予約有効フラグ */
  @Column(name = "reservation_active_flag")
  private Boolean reservationActiveFlag;

  /** 予約事業者ID */
  @Column(name = "reserve_provider_id")
  private UUID reserveProviderId;

  /** 対応する機体情報 */
  @ManyToOne
  @JoinColumn(
      name = "aircraft_id",
      referencedColumnName = "aircraft_id",
      insertable = false,
      updatable = false)
  @Fetch(FetchMode.JOIN)
  private AircraftInfoEntity aircraftInfoEntity;

  /** 対応する離着陸場情報 */
  @ManyToOne
  @JoinColumn(
      name = "droneport_id",
      referencedColumnName = "droneport_id",
      insertable = false,
      updatable = false)
  @Fetch(FetchMode.JOIN)
  private DronePortInfoEntity dronePortInfoEntity;

  /** 対応する離着陸場状態 */
  @ManyToOne
  @JoinColumn(
      name = "droneport_id",
      referencedColumnName = "droneport_id",
      insertable = false,
      updatable = false)
  @Fetch(FetchMode.JOIN)
  private DronePortStatusEntity dronePortStatusEntity;
}
