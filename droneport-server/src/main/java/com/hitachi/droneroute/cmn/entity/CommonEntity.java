package com.hitachi.droneroute.cmn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

/** 離着陸場管理/機体管理共通エンティティ */
@Setter
@Getter
@MappedSuperclass
public class CommonEntity {

  /** オペレータID */
  @Column(name = "operator_id")
  private String operatorId;

  /** 更新者ID */
  @Column(name = "update_user_id")
  private String updateUserId;

  /** 登録日時 */
  @Column(name = "create_time")
  private Timestamp createTime;

  /** 更新日時 */
  @Column(name = "update_time")
  private Timestamp updateTime;

  /** 削除フラグ */
  @Column(name = "delete_flag")
  private Boolean deleteFlag;
}
