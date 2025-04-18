package com.hitachi.droneroute.dpm.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ドローンポート情報一覧取得応答
 * @author dpls01
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortInfoListResponseDto {
	
	/**
	 * ドローンポート情報リスト
	 */
	private List<DronePortInfoListResponseElement> data;
	
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
