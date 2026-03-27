package com.hitachi.droneroute.arm.entity;

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

/** 機体予約情報Entity */
@Setter
@Getter
@Table(name = "aircraft_reserve_info")
@Entity
@NamedEntityGraph(name = "aircraft_reserve_info_join", includeAllAttributes = true)
public class AircraftReserveInfoEntity extends CommonEntity {
  /** 機体予約ID */
  @Id
  @Column(name = "aircraft_reserve_id", length = 12)
  private UUID aircraftReservationId;

  /** 一括予約ID */
  @Column(name = "group_reservation_id", length = 12)
  private UUID groupReservationId;

  /** 機体ID */
  @Column(name = "aircraft_id", length = 12)
  private UUID aircraftId;

  /** 予約日時範囲 */
  @Type(PostgreSQLRangeType.class)
  @Column(name = "reservation_time", columnDefinition = "tsrange")
  private Range<LocalDateTime> reservationTime;

  /** 予約事業者ID */
  @Column(name = "reserve_provider_id", length = 12)
  private UUID reserveProviderId;

  /** 対応する機体情報 */
  @ManyToOne
  @JoinColumn(name = "aircraft_id", insertable = false, updatable = false)
  @Fetch(FetchMode.JOIN)
  private AircraftInfoEntity aircraftEntity;
}
