package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 機体予約情報一覧応答のDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AircraftReserveInfoListResponseDto {
	
	/**
	 * 機体予約情報リスト
	 */
	private List<AircraftReserveInfoDetailResponseDto> data;
	
	/**
	 * 1ページ当たりの件数
	 */
	@JsonInclude(Include.NON_NULL)
	private Integer perPage;
	
	/**
	 * 現在ページ番号
	 */
	@JsonInclude(Include.NON_NULL)
	private Integer currentPage;
	
	/**
	 * 最終ページ番号
	 */
	@JsonInclude(Include.NON_NULL)
	private Integer lastPage;
	
	/**
	 * 全体件数
	 */
	@JsonInclude(Include.NON_NULL)
	private Integer total;
}
