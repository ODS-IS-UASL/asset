package com.hitachi.droneroute.prm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListDetailElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListResponseDto;
import com.hitachi.droneroute.prm.service.PriceInfoSearchListService;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/** PriceInfoControllerTestの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class PriceInfoControllerTest {

  @Value("${droneroute.basepath}/price/info")
  String basePath;

  MockMvc mockMvc;

  @Autowired WebApplicationContext webApplicationContext;

  @MockBean private PriceInfoSearchListService service;

  @SpyBean private SystemSettings systemSettings;

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: クエリパラメータなしでのリソース料金情報検索<br>
   * 条件: クエリパラメータなし<br>
   * 結果: レスポンスが正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void resourcePriceList_クエリパラメータなし() throws Exception {

    PriceInfoSearchListResponseDto responseDto = new PriceInfoSearchListResponseDto();
    responseDto.setResources(new ArrayList<>());

    responseDto = createPriceInfoSearchListResponseDto(1);
    when(service.getPriceInfoList(any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resources[0].resourceId").value("リソースID"))
            .andExpect(jsonPath("$.resources[0].resourceType").value(1))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].priceId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priceType").value(4))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].pricePerUnit").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].price").value(1000))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveStartTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveEndTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priority").value(1))
            .andReturn();

    // Assert
    verify(service, times(1)).getPriceInfoList(any());

    // キャプチャ
    ArgumentCaptor<PriceInfoSearchListRequestDto> cap =
        ArgumentCaptor.forClass(PriceInfoSearchListRequestDto.class);
    verify(service).getPriceInfoList(cap.capture());
    assertEquals(null, cap.getValue().getPriceId());
    assertEquals(null, cap.getValue().getResourceId());
    assertEquals(null, cap.getValue().getResourceType());
    assertEquals(null, cap.getValue().getPriceType());
    assertEquals(null, cap.getValue().getPricePerUnitFrom());
    assertEquals(null, cap.getValue().getPricePerUnitTo());
    assertEquals(null, cap.getValue().getPriceFrom());
    assertEquals(null, cap.getValue().getPriceTo());
    assertEquals(null, cap.getValue().getEffectiveStartTime());
    assertEquals(null, cap.getValue().getEffectiveEndTime());
    assertEquals(null, cap.getValue().getSortOrders());
    assertEquals(null, cap.getValue().getSortColumns());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: 検索結果1件のリソース料金情報検索<br>
   * 条件: serviceの検索結果が1件<br>
   * 結果: レスポンスが正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void resourcePriceList_正常1件() throws Exception {

    PriceInfoSearchListResponseDto responseDto = new PriceInfoSearchListResponseDto();
    responseDto.setResources(new ArrayList<>());

    responseDto = createPriceInfoSearchListResponseDto(1);

    when(service.getPriceInfoList(any())).thenReturn(responseDto);

    PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
    String query =
        "priceId="
            + dto.getPriceId()
            + "&resourceId="
            + dto.getResourceId()
            + "&resourceType="
            + dto.getResourceType()
            + "&priceType="
            + dto.getPriceType()
            + "&pricePerUnitFrom="
            + dto.getPricePerUnitFrom()
            + "&pricePerUnitTo="
            + dto.getPricePerUnitTo()
            + "&priceFrom="
            + dto.getPriceFrom()
            + "&priceTo="
            + dto.getPriceTo()
            + "&effectiveStartTime="
            + dto.getEffectiveStartTime()
            + "&effectiveEndTime="
            + dto.getEffectiveEndTime()
            + "&sortOrders="
            + dto.getSortOrders()
            + "&sortColumns="
            + dto.getSortColumns();

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?" + query))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resources[0].resourceId").value("リソースID"))
            .andExpect(jsonPath("$.resources[0].resourceType").value(1))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].priceId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priceType").value(4))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].pricePerUnit").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].price").value(1000))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveStartTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveEndTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priority").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].operatorId").value("ope01"))
            .andReturn();

    // Assert
    verify(service, times(1)).getPriceInfoList(any());

    // キャプチャ
    ArgumentCaptor<PriceInfoSearchListRequestDto> cap =
        ArgumentCaptor.forClass(PriceInfoSearchListRequestDto.class);
    verify(service).getPriceInfoList(cap.capture());
    assertEquals(dto.getPriceId(), cap.getValue().getPriceId());
    assertEquals(dto.getResourceId(), cap.getValue().getResourceId());
    assertEquals(dto.getResourceType(), cap.getValue().getResourceType());
    assertEquals(dto.getPriceType(), cap.getValue().getPriceType());
    assertEquals(dto.getPricePerUnitFrom(), cap.getValue().getPricePerUnitFrom());
    assertEquals(dto.getPricePerUnitTo(), cap.getValue().getPricePerUnitTo());
    assertEquals(dto.getPriceFrom(), cap.getValue().getPriceFrom());
    assertEquals(dto.getPriceTo(), cap.getValue().getPriceTo());
    assertEquals(dto.getEffectiveStartTime(), cap.getValue().getEffectiveStartTime());
    assertEquals(dto.getEffectiveEndTime(), cap.getValue().getEffectiveEndTime());
    assertEquals(dto.getSortOrders(), cap.getValue().getSortOrders());
    assertEquals(dto.getSortColumns(), cap.getValue().getSortColumns());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: 検索結果3件のリソース料金情報検索<br>
   * 条件: serviceの検索結果が3件<br>
   * 結果: レスポンスが正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void resourcePriceList_正常3件() throws Exception {

    PriceInfoSearchListResponseDto responseDto = new PriceInfoSearchListResponseDto();
    responseDto.setResources(new ArrayList<>());

    responseDto = createPriceInfoSearchListResponseDto(3);
    when(service.getPriceInfoList(any())).thenReturn(responseDto);

    PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
    String query =
        "priceId="
            + dto.getPriceId()
            + "&resourceId="
            + dto.getResourceId()
            + "&resourceType="
            + dto.getResourceType()
            + "&priceType="
            + dto.getPriceType()
            + "&pricePerUnitFrom="
            + dto.getPricePerUnitFrom()
            + "&pricePerUnitTo="
            + dto.getPricePerUnitTo()
            + "&priceFrom="
            + dto.getPriceFrom()
            + "&priceTo="
            + dto.getPriceTo()
            + "&effectiveStartTime="
            + dto.getEffectiveStartTime()
            + "&effectiveEndTime="
            + dto.getEffectiveEndTime()
            + "&priority="
            + "&sortOrders="
            + dto.getSortOrders()
            + "&sortColumns="
            + dto.getSortColumns();

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?" + query))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resources[0].resourceId").value("リソースID"))
            .andExpect(jsonPath("$.resources[0].resourceType").value(1))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].priceId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priceType").value(4))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].pricePerUnit").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].price").value(1000))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveStartTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveEndTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priority").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].operatorId").value("ope01"))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[1].priceId")
                    .value("6b5ec052-a76f-87cb-ef4a-a31c62a13276"))
            .andExpect(jsonPath("$.resources[0].priceInfos[1].priceType").value(4))
            .andExpect(jsonPath("$.resources[0].priceInfos[1].pricePerUnit").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[1].price").value(2000))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[1].effectiveStartTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[1].effectiveEndTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(jsonPath("$.resources[0].priceInfos[1].priority").value(2))
            .andExpect(jsonPath("$.resources[0].priceInfos[1].operatorId").value("ope01"))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[2].priceId")
                    .value("f3113bed-357e-2386-2b15-effaf01a592e"))
            .andExpect(jsonPath("$.resources[0].priceInfos[2].priceType").value(4))
            .andExpect(jsonPath("$.resources[0].priceInfos[2].pricePerUnit").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[2].price").value(3000))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[2].effectiveStartTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[2].effectiveEndTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(jsonPath("$.resources[0].priceInfos[2].priority").value(3))
            .andExpect(jsonPath("$.resources[0].priceInfos[2].operatorId").value("ope01"))
            .andReturn();

    // Assert
    verify(service, times(1)).getPriceInfoList(any());

    // キャプチャ
    ArgumentCaptor<PriceInfoSearchListRequestDto> cap =
        ArgumentCaptor.forClass(PriceInfoSearchListRequestDto.class);
    verify(service).getPriceInfoList(cap.capture());
    assertEquals(dto.getPriceId(), cap.getValue().getPriceId());
    assertEquals(dto.getResourceId(), cap.getValue().getResourceId());
    assertEquals(dto.getResourceType(), cap.getValue().getResourceType());
    assertEquals(dto.getPriceType(), cap.getValue().getPriceType());
    assertEquals(dto.getPricePerUnitFrom(), cap.getValue().getPricePerUnitFrom());
    assertEquals(dto.getPricePerUnitTo(), cap.getValue().getPricePerUnitTo());
    assertEquals(dto.getPriceFrom(), cap.getValue().getPriceFrom());
    assertEquals(dto.getPriceTo(), cap.getValue().getPriceTo());
    assertEquals(dto.getEffectiveStartTime(), cap.getValue().getEffectiveStartTime());
    assertEquals(dto.getEffectiveEndTime(), cap.getValue().getEffectiveEndTime());
    assertEquals(dto.getSortOrders(), cap.getValue().getSortOrders());
    assertEquals(dto.getSortColumns(), cap.getValue().getSortColumns());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: 検索結果0件のリソース料金情報検索<br>
   * 条件: serviceの検索結果が0件<br>
   * 結果: レスポンスが正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void resourcePriceList_正常_結果0件() throws Exception {
    PriceInfoSearchListResponseDto responseDto = new PriceInfoSearchListResponseDto();
    responseDto.setResources(new ArrayList<>());
    when(service.getPriceInfoList(any())).thenReturn(responseDto);

    PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
    String query =
        "priceId="
            + dto.getPriceId()
            + "&resourceId="
            + dto.getResourceId()
            + "&resourceType="
            + dto.getResourceType()
            + "&priceType="
            + dto.getPriceType()
            + "&pricePerUnitFrom="
            + dto.getPricePerUnitFrom()
            + "&pricePerUnitTo="
            + dto.getPricePerUnitTo()
            + "&priceFrom="
            + dto.getPriceFrom()
            + "&priceTo="
            + dto.getPriceTo()
            + "&effectiveStartTime="
            + dto.getEffectiveStartTime()
            + "&effectiveEndTime="
            + dto.getEffectiveEndTime()
            + "&sortOrders="
            + dto.getSortOrders()
            + "&sortColumns="
            + dto.getSortColumns();

    // Act
    MvcResult response =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?" + query))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resources.length()").value(0))
            .andReturn();

    // Assert
    verify(service, times(1)).getPriceInfoList(any());

    // キャプチャ
    ArgumentCaptor<PriceInfoSearchListRequestDto> cap =
        ArgumentCaptor.forClass(PriceInfoSearchListRequestDto.class);
    verify(service).getPriceInfoList(cap.capture());
    assertEquals(dto.getPriceId(), cap.getValue().getPriceId());
    assertEquals(dto.getResourceId(), cap.getValue().getResourceId());
    assertEquals(dto.getResourceType(), cap.getValue().getResourceType());
    assertEquals(dto.getPriceType(), cap.getValue().getPriceType());
    assertEquals(dto.getPricePerUnitFrom(), cap.getValue().getPricePerUnitFrom());
    assertEquals(dto.getPricePerUnitTo(), cap.getValue().getPricePerUnitTo());
    assertEquals(dto.getPriceFrom(), cap.getValue().getPriceFrom());
    assertEquals(dto.getPriceTo(), cap.getValue().getPriceTo());
    assertEquals(dto.getEffectiveStartTime(), cap.getValue().getEffectiveStartTime());
    assertEquals(dto.getEffectiveEndTime(), cap.getValue().getEffectiveEndTime());
    assertEquals(dto.getSortOrders(), cap.getValue().getSortOrders());
    assertEquals(dto.getSortColumns(), cap.getValue().getSortOrders());
    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: ソート条件ありでのリソース料金情報検索<br>
   * 条件: serviceの検索結果が1件 ソート条件あり<br>
   * 結果: レスポンスが正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void resourcePriceList_正常ソート条件あり() throws Exception {

    PriceInfoSearchListResponseDto responseDto = new PriceInfoSearchListResponseDto();
    responseDto.setResources(new ArrayList<>());

    responseDto = createPriceInfoSearchListResponseDto(1);

    when(service.getPriceInfoList(any())).thenReturn(responseDto);

    PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
    dto.setSortOrders("0");
    dto.setSortColumns("resourceId");

    String query =
        "priceId="
            + dto.getPriceId()
            + "&resourceId="
            + dto.getResourceId()
            + "&resourceType="
            + dto.getResourceType()
            + "&priceType="
            + dto.getPriceType()
            + "&pricePerUnitFrom="
            + dto.getPricePerUnitFrom()
            + "&pricePerUnitTo="
            + dto.getPricePerUnitTo()
            + "&priceFrom="
            + dto.getPriceFrom()
            + "&priceTo="
            + dto.getPriceTo()
            + "&effectiveStartTime="
            + dto.getEffectiveStartTime()
            + "&effectiveEndTime="
            + dto.getEffectiveEndTime()
            + "&sortOrders="
            + dto.getSortOrders()
            + "&sortColumns="
            + dto.getSortColumns();

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?" + query))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resources[0].resourceId").value("リソースID"))
            .andExpect(jsonPath("$.resources[0].resourceType").value(1))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].priceId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priceType").value(4))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].pricePerUnit").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].price").value(1000))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveStartTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveEndTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priority").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].operatorId").value("ope01"))
            .andReturn();

    // Assert
    verify(service, times(1)).getPriceInfoList(any());

    // キャプチャ
    ArgumentCaptor<PriceInfoSearchListRequestDto> cap =
        ArgumentCaptor.forClass(PriceInfoSearchListRequestDto.class);
    verify(service).getPriceInfoList(cap.capture());
    assertEquals(dto.getPriceId(), cap.getValue().getPriceId());
    assertEquals(dto.getResourceId(), cap.getValue().getResourceId());
    assertEquals(dto.getResourceType(), cap.getValue().getResourceType());
    assertEquals(dto.getPriceType(), cap.getValue().getPriceType());
    assertEquals(dto.getPricePerUnitFrom(), cap.getValue().getPricePerUnitFrom());
    assertEquals(dto.getPricePerUnitTo(), cap.getValue().getPricePerUnitTo());
    assertEquals(dto.getPriceFrom(), cap.getValue().getPriceFrom());
    assertEquals(dto.getPriceTo(), cap.getValue().getPriceTo());
    assertEquals(dto.getEffectiveStartTime(), cap.getValue().getEffectiveStartTime());
    assertEquals(dto.getEffectiveEndTime(), cap.getValue().getEffectiveEndTime());
    assertEquals("0", cap.getValue().getSortOrders());
    assertEquals("resourceId", cap.getValue().getSortColumns());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: 定義外項目の無視<br>
   * 条件: クエリに未定義キー teigigaiCol を追加<br>
   * 結果: レスポンスが正しく返される（定義外項目は無視される）<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void resourcePriceList_正常_定義外項目あり無視() throws Exception {
    PriceInfoSearchListResponseDto responseDto = createPriceInfoSearchListResponseDto(1);
    when(service.getPriceInfoList(any())).thenReturn(responseDto);

    PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
    String query =
        "priceId="
            + dto.getPriceId()
            + "&teigigaiCol=IGNORED" // 未定義項目
            + "&resourceId="
            + dto.getResourceId()
            + "&resourceType="
            + dto.getResourceType()
            + "&priceType="
            + dto.getPriceType()
            + "&pricePerUnitFrom="
            + dto.getPricePerUnitFrom()
            + "&pricePerUnitTo="
            + dto.getPricePerUnitTo()
            + "&priceFrom="
            + dto.getPriceFrom()
            + "&priceTo="
            + dto.getPriceTo()
            + "&effectiveStartTime="
            + dto.getEffectiveStartTime()
            + "&effectiveEndTime="
            + dto.getEffectiveEndTime()
            + "&sortOrders="
            + dto.getSortOrders()
            + "&sortColumns="
            + dto.getSortColumns();

    // Act
    MvcResult res =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?" + query))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].priceId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    // Assert
    verify(service, times(1)).getPriceInfoList(any());

    // キャプチャ
    ArgumentCaptor<PriceInfoSearchListRequestDto> cap =
        ArgumentCaptor.forClass(PriceInfoSearchListRequestDto.class);
    verify(service).getPriceInfoList(cap.capture());
    assertEquals(dto.getPriceId(), cap.getValue().getPriceId());
    assertEquals(dto.getResourceId(), cap.getValue().getResourceId());
    assertEquals(dto.getResourceType(), cap.getValue().getResourceType());
    assertEquals(dto.getPriceType(), cap.getValue().getPriceType());
    assertEquals(dto.getPricePerUnitFrom(), cap.getValue().getPricePerUnitFrom());
    assertEquals(dto.getPricePerUnitTo(), cap.getValue().getPricePerUnitTo());
    assertEquals(dto.getPriceFrom(), cap.getValue().getPriceFrom());
    assertEquals(dto.getPriceTo(), cap.getValue().getPriceTo());
    assertEquals(dto.getEffectiveStartTime(), cap.getValue().getEffectiveStartTime());
    assertEquals(dto.getEffectiveEndTime(), cap.getValue().getEffectiveEndTime());
    assertEquals(dto.getSortOrders(), cap.getValue().getSortOrders());
    assertEquals(dto.getSortColumns(), cap.getValue().getSortColumns());

    log.info(res.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: 文字項目への数値指定が文字列として扱われることの確認<br>
   * 条件: priceId に数値 123 を指定<br>
   * 結果: レスポンスが正しく返される（文字列として扱われる）<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void resourcePriceList_正常_文字項目へ数値指定無視() throws Exception {
    PriceInfoSearchListResponseDto responseDto = createPriceInfoSearchListResponseDto(1);
    when(service.getPriceInfoList(any())).thenReturn(responseDto);

    String query = "priceId=123"; // 本来文字項目

    // Act
    MvcResult res =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?" + query))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].priceId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    // Assert
    verify(service, times(1)).getPriceInfoList(any());

    // キャプチャ
    ArgumentCaptor<PriceInfoSearchListRequestDto> cap =
        ArgumentCaptor.forClass(PriceInfoSearchListRequestDto.class);
    verify(service).getPriceInfoList(cap.capture());
    assertEquals("123", cap.getValue().getPriceId());

    log.info(res.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: Controllerでの数値範囲外値の通過<br>
   * 条件:
   * resourceType、priceType、pricePerUnitFrom、pricePerUnitTo、priceFrom、priceToに値域外の数値(9999999999)を設定
   * <br>
   * 結果: Controllerはバリデーションを実施しないため、値域外の数値でもステータス200が返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void resourcePriceList_正常_数値範囲外でもController通過() throws Exception {

    PriceInfoSearchListResponseDto responseDto = new PriceInfoSearchListResponseDto();
    responseDto.setResources(new ArrayList<>());

    responseDto = createPriceInfoSearchListResponseDto(1);

    when(service.getPriceInfoList(any())).thenReturn(responseDto);

    String outOfRangeValue = "9999999999";
    String query =
        "priceId=0a0711a5-ff74-4164-9309-8888b433cf22"
            + "&resourceId=リソースID"
            + "&resourceType="
            + outOfRangeValue
            + "&priceType="
            + outOfRangeValue
            + "&pricePerUnitFrom="
            + outOfRangeValue
            + "&pricePerUnitTo="
            + outOfRangeValue
            + "&priceFrom="
            + outOfRangeValue
            + "&priceTo="
            + outOfRangeValue
            + "&effectiveStartTime=2025-11-13T10:00:00Z"
            + "&effectiveEndTime=2025-11-13T10:00:00Z"
            + "&sortOrders="
            + "&sortColumns=";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?" + query))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resources[0].resourceId").value("リソースID"))
            .andExpect(jsonPath("$.resources[0].resourceType").value(1))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].priceId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priceType").value(4))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].pricePerUnit").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].price").value(1000))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveStartTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(
                jsonPath("$.resources[0].priceInfos[0].effectiveEndTime")
                    .value("2025-11-13T10:00:00Z"))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].priority").value(1))
            .andExpect(jsonPath("$.resources[0].priceInfos[0].operatorId").value("ope01"))
            .andReturn();

    // Assert
    verify(service, times(1)).getPriceInfoList(any());

    // キャプチャ - 値域外の数値がBigIntegerとして正しく渡されることを確認
    ArgumentCaptor<PriceInfoSearchListRequestDto> cap =
        ArgumentCaptor.forClass(PriceInfoSearchListRequestDto.class);
    verify(service).getPriceInfoList(cap.capture());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", cap.getValue().getPriceId());
    assertEquals("リソースID", cap.getValue().getResourceId());
    assertEquals(new BigInteger(outOfRangeValue), cap.getValue().getResourceType());
    assertEquals(new BigInteger(outOfRangeValue), cap.getValue().getPriceType());
    assertEquals(new BigInteger(outOfRangeValue), cap.getValue().getPricePerUnitFrom());
    assertEquals(new BigInteger(outOfRangeValue), cap.getValue().getPricePerUnitTo());
    assertEquals(new BigInteger(outOfRangeValue), cap.getValue().getPriceFrom());
    assertEquals(new BigInteger(outOfRangeValue), cap.getValue().getPriceTo());
    assertEquals("2025-11-13T10:00:00Z", cap.getValue().getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", cap.getValue().getEffectiveEndTime());
    assertEquals("", cap.getValue().getSortOrders());
    assertEquals("", cap.getValue().getSortColumns());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: 数値項目への文字指定による型変換エラー<br>
   * 条件: resourceType=a<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void resourcePriceList_異常_数値項目に文字() throws Exception {
    // Act
    MvcResult res =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?resourceType=a"))
            .andExpect(status().isInternalServerError())
            .andReturn();

    // Assert
    verify(service, times(0)).getPriceInfoList(any());
    log.info(res.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: resourcePriceList<br>
   * 試験名: サービスエラー発生時のエラーハンドリング<br>
   * 条件: service.getPriceInfoList が ServiceErrorException を送出<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void resourcePriceList_異常_サービスエラー発生() throws Exception {

    doThrow(new ServiceErrorException("ServiceErrorMessage")).when(service).getPriceInfoList(any());

    // Act
    MvcResult res =
        mockMvc
            .perform(get(basePath + "/resourcePriceList?"))
            .andExpect(status().isInternalServerError())
            .andReturn();

    // Assert
    verify(service, times(1)).getPriceInfoList(any());
    log.info(res.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * 料金情報一覧用リクエストDTO作成
   *
   * @return
   */
  private PriceInfoSearchListRequestDto createPriceInfoSearchListRequestDto() {
    PriceInfoSearchListRequestDto ret = new PriceInfoSearchListRequestDto();
    ret.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setResourceId("リソースID");
    ret.setResourceType(BigInteger.valueOf(1));
    ret.setPriceType(BigInteger.valueOf(4));
    ret.setPricePerUnitFrom(BigInteger.valueOf(1));
    ret.setPricePerUnitTo(BigInteger.valueOf(2));
    ret.setPriceFrom(BigInteger.valueOf(1000));
    ret.setPriceTo(BigInteger.valueOf(2000));
    ret.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret.setEffectiveEndTime("2025-11-13T10:00:00Z");
    ret.setSortOrders("");
    ret.setSortColumns("");

    return ret;
  }

  /**
   * 料金情報一覧用レスポンスDTO作成
   *
   * @return
   */
  private PriceInfoSearchListResponseDto createPriceInfoSearchListResponseDto(int cnt) {
    PriceInfoSearchListResponseDto ret = new PriceInfoSearchListResponseDto();
    String[] priceIds = {
      "0a0711a5-ff74-4164-9309-8888b433cf22",
      "6b5ec052-a76f-87cb-ef4a-a31c62a13276",
      "f3113bed-357e-2386-2b15-effaf01a592e"
    };

    // 料金情報
    List<PriceInfoSearchListDetailElement> eleDetailList = new ArrayList<>();
    for (int i = 0; i < cnt; i++) {
      PriceInfoSearchListDetailElement eleDetail = new PriceInfoSearchListDetailElement();
      eleDetail.setPriceId(priceIds[i]);
      eleDetail.setPriceType(4);
      eleDetail.setPricePerUnit(1);
      eleDetail.setPrice((i + 1) * 1000);
      eleDetail.setEffectiveStartTime("2025-11-13T10:00:00Z");
      eleDetail.setEffectiveEndTime("2025-11-13T10:00:00Z");
      eleDetail.setPriority(i + 1);
      eleDetail.setOperatorId("ope01");

      eleDetailList.add(eleDetail);
    }

    // 料金設定リソース
    List<PriceInfoSearchListElement> eleList = new ArrayList<>();
    PriceInfoSearchListElement ele = new PriceInfoSearchListElement();
    ele.setResourceId("リソースID");
    ele.setResourceType(1);
    ele.setPriceInfos(eleDetailList);
    eleList.add(ele);
    ret.setResources(eleList);

    return ret;
  }
}
