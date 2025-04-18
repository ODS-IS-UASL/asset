package com.hitachi.droneroute.arm.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 機体情報一覧応答のDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AircraftInfoSearchListResponseDto {
	
	/**
	 * 機体情報リスト
	 */
	private List<AircraftInfoSearchListElement> data;

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
