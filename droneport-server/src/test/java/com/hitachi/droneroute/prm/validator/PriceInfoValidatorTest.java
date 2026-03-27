package com.hitachi.droneroute.prm.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.prm.constants.PriceInfoConstants;
import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

/** PriceInfoValidatorクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
public class PriceInfoValidatorTest {

  @Autowired private PriceInfoValidator validator;

  @SpyBean private SystemSettings systemSettings;

  /**
   * メソッド名: validateForRegist<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   */
  static Stream<Arguments> RegistNmlCase() {
    return Stream.of(
        Arguments.of(
            "テンプレート変更なし",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  return dto;
                }),
        Arguments.of(
            "リソース種別が10(離着陸場)、リソースIDがUUID以外の文字列の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
                  dto.setResourceId("notUUID");
                  return dto;
                }),
        Arguments.of(
            "リソース種別が30(機体)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setResourceType(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT);
                  dto.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf22");
                  return dto;
                }),
        Arguments.of(
            "料金タイプが1の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(1);
                  return dto;
                }),
        Arguments.of(
            "料金タイプが2の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(2);
                  return dto;
                }),
        Arguments.of(
            "料金タイプが3の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(3);
                  return dto;
                }),
        Arguments.of(
            "料金タイプが4の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(4);
                  return dto;
                }),
        Arguments.of(
            "料金タイプが5の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(5);
                  return dto;
                }),
        Arguments.of(
            "料金タイプが6の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(6);
                  return dto;
                }),
        Arguments.of(
            "料金タイプが7の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(7);
                  return dto;
                }),
        Arguments.of(
            "料金単位が1の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPricePerUnit(1);
                  return dto;
                }),
        Arguments.of(
            "料金単位が2147483647の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPricePerUnit(2147483647);
                  return dto;
                }),
        Arguments.of(
            "料金が1の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPrice(1);
                  return dto;
                }),
        Arguments.of(
            "料金が2147483647の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPrice(2147483647);
                  return dto;
                }),
        Arguments.of(
            "優先度が1の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriority(1);
                  return dto;
                }),
        Arguments.of(
            "優先度が100の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriority(100);
                  return dto;
                }),
        Arguments.of(
            "処理種別が1の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setProcessingType(1);
                  return dto;
                }),
        Arguments.of(
            "処理種別が2の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createUpdatePriceInfoRequestDto();
                  dto.setProcessingType(2);
                  return dto;
                }),
        Arguments.of(
            "処理種別が3の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createDeletePriceInfoRequestDto();
                  dto.setProcessingType(3);
                  return dto;
                }));
  }

  /**
   * メソッド名: validateForRegist<br>
   * ParameterizedTest用の引数準備メソッド<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   * 第3引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> RegistErrCase() {
    return Stream.of(
        Arguments.of(
            "リソース種別がnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setResourceType(null);
                  return dto;
                },
            "[1番目のリソース種別に値が設定されていません。]"),
        Arguments.of(
            "リソースIDがnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setResourceId(null);
                  return dto;
                },
            "[1番目のリソースIDに値が設定されていません。]"),
        Arguments.of(
            "リソースIDが空文字の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setResourceId("");
                  return dto;
                },
            "[1番目のリソースIDに値が設定されていません。]"),
        Arguments.of(
            "リソース種別が20(機体)、リソースIDが文字列の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setResourceType(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT);
                  dto.setResourceId("notUUID");
                  return dto;
                },
            "[1番目のリソースIDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "リソース種別が19(値域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setResourceType(19);
                  dto.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf22");
                  return dto;
                },
            "[1番目のリソース種別の値が不正です。\n範囲[20, 30]]"),
        Arguments.of(
            "リソース種別が21(値域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setResourceType(21);
                  dto.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf22");
                  return dto;
                },
            "[1番目のリソース種別の値が不正です。\n範囲[20, 30]]"),
        Arguments.of(
            "リソース種別が31(値域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setResourceType(31);
                  dto.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf22");
                  return dto;
                },
            "[1番目のリソース種別の値が不正です。\n範囲[20, 30]]"),
        Arguments.of(
            "料金タイプがnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(null);
                  return dto;
                },
            "[1番目の料金タイプに値が設定されていません。]"),
        Arguments.of(
            "料金タイプが0(値域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(0);
                  return dto;
                },
            "[1番目の料金タイプの値が不正です。\n範囲[1, 2, 3, 4, 5, 6, 7]]"),
        Arguments.of(
            "料金タイプが8(値域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriceType(8);
                  return dto;
                },
            "[1番目の料金タイプの値が不正です。\n範囲[1, 2, 3, 4, 5, 6, 7]]"),
        Arguments.of(
            "料金単位がnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPricePerUnit(null);
                  return dto;
                },
            "[1番目の料金単位に値が設定されていません。]"),
        Arguments.of(
            "料金単位が0(値域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPricePerUnit(0);
                  return dto;
                },
            "[1番目の料金単位の値が不正です。\n最小値(1)、最大値(2,147,483,647)]"),
        Arguments.of(
            "料金単位が0(値域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPricePerUnit(0);
                  return dto;
                },
            "[1番目の料金単位の値が不正です。\n最小値(1)、最大値(2,147,483,647)]"),
        Arguments.of(
            "料金がnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPrice(null);
                  return dto;
                },
            "[1番目の料金に値が設定されていません。]"),
        Arguments.of(
            "料金が-1(値域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPrice(-1);
                  return dto;
                },
            "[1番目の料金の値が不正です。\n最小値(0)、最大値(2,147,483,647)]"),
        Arguments.of(
            "適用開始日時がnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setEffectiveStartTime(null);
                  return dto;
                },
            "[1番目の適用開始時間に値が設定されていません。]"),
        Arguments.of(
            "適用開始日時が空文字の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setEffectiveStartTime("");
                  return dto;
                },
            "[1番目の適用開始時間に値が設定されていません。]"),
        Arguments.of(
            "適用開始日時がフォーマット不正の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setEffectiveStartTime("2025/11/15T01:23:45Z");
                  return dto;
                },
            "[1番目の適用開始時間がサポートされていない形式です。\n入力値:2025/11/15T01:23:45Z]"),
        Arguments.of(
            "適用終了日時がnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setEffectiveEndTime(null);
                  return dto;
                },
            "[1番目の適用終了時間に値が設定されていません。]"),
        Arguments.of(
            "適用終了日時が空文字の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setEffectiveEndTime("");
                  return dto;
                },
            "[1番目の適用終了時間に値が設定されていません。]"),
        Arguments.of(
            "適用終了日時がフォーマット不正の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setEffectiveEndTime("2025/11/15T01:23:45Z");
                  return dto;
                },
            "[1番目の適用終了時間がサポートされていない形式です。\n入力値:2025/11/15T01:23:45Z]"),
        Arguments.of(
            "適用開始日時が適用終了日時より遅い時刻の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setEffectiveStartTime("2025-11-15T11:00:00Z");
                  dto.setEffectiveEndTime("2025-11-15T10:00:00Z");
                  return dto;
                },
            "[1番目の適用開始日時が適用終了日時よりも未来の日時になっています。1番目の適用開始日時(2025-11-15T11:00:00Z),適用終了日時(2025-11-15T10:00:00Z)]"),
        Arguments.of(
            "優先度がnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriority(null);
                  return dto;
                },
            "[1番目の優先度に値が設定されていません。]"),
        Arguments.of(
            "優先度が0(領域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriority(0);
                  return dto;
                },
            "[1番目の優先度の値が不正です。\n最小値(1)、最大値(100)]"),
        Arguments.of(
            "優先度が101(領域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setPriority(101);
                  return dto;
                },
            "[1番目の優先度の値が不正です。\n最小値(1)、最大値(100)]"),
        Arguments.of(
            "オペレータIDがnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setOperatorId(null);
                  return dto;
                },
            "[1番目のオペレータIDに値が設定されていません。]"),
        Arguments.of(
            "オペレータIDが空文字の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setOperatorId("");
                  return dto;
                },
            "[1番目のオペレータIDに値が設定されていません。]"),
        Arguments.of(
            "処理種別がnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setProcessingType(null);
                  return dto;
                },
            "[1番目の処理種別に値が設定されていません。]"),
        Arguments.of(
            "処理種別が0(領域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setProcessingType(0);
                  return dto;
                },
            "[1番目の処理種別の値が不正です。\n範囲[1, 2, 3]]"),
        Arguments.of(
            "処理種別が4(領域外)の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setProcessingType(0);
                  return dto;
                },
            "[1番目の処理種別の値が不正です。\n範囲[1, 2, 3]]"),
        Arguments.of(
            "適用開始日時と適用終了日時が同じ時間の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createRegistPriceInfoRequestDto();
                  dto.setEffectiveStartTime("2025-11-15T11:00:00Z");
                  dto.setEffectiveEndTime("2025-11-15T11:00:00Z");
                  return dto;
                },
            "[1番目の適用開始時間と適用終了時間が同一の日時になっています。1番目の適用開始時間(2025-11-15T11:00:00Z),適用終了時間(2025-11-15T11:00:00Z)]"));
  }

  /** validateForRegist 正常系パラメタライズドテスト */
  @ParameterizedTest(name = "{0}")
  @MethodSource("RegistNmlCase")
  public void testValidateForRegist_nmlCase(String caseName, Supplier<PriceInfoRequestDto> sDto) {
    PriceInfoRequestDto dto = sDto.get();
    List<PriceInfoRequestDto> dtoList = new ArrayList<>();
    dtoList.add(dto);
    assertDoesNotThrow(() -> validator.validateAll(dtoList));
  }

  /** validateForRegist 異常系パラメタライズドテスト */
  @ParameterizedTest(name = "{0}")
  @MethodSource("RegistErrCase")
  public void testValidateForRegist_errCase(
      String caseName, Supplier<PriceInfoRequestDto> sDto, String msg) {
    PriceInfoRequestDto dto = sDto.get();
    List<PriceInfoRequestDto> dtoList = new ArrayList<>();
    dtoList.add(dto);
    Exception ex =
        assertThrows(ValidationErrorException.class, () -> validator.validateAll(dtoList));
    assertEquals(msg, ex.getMessage());
  }

  /** データテンプレート ■登録更新リクエスト 登録更新削除_正常リクエストボディ */
  private static PriceInfoRequestDto createRegistPriceInfoRequestDto() {
    PriceInfoRequestDto ret = new PriceInfoRequestDto();
    ret.setProcessingType(1);
    ret.setResourceId("リソースID");
    ret.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
    ret.setPriceType(4);
    ret.setPricePerUnit(1);
    ret.setPrice(1000);
    ret.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret.setEffectiveEndTime("2025-11-13T11:00:00Z");
    ret.setPriority(1);
    ret.setOperatorId("ope01");
    ret.setRowNumber(1);

    return ret;
  }

  /**
   * メソッド名: validateForUpdate<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   */
  static Stream<Arguments> UpdateNmlCase() {
    return Stream.of(
        Arguments.of(
            "テンプレート変更なし",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createUpdatePriceInfoRequestDto();
                  return dto;
                }));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * ParameterizedTest用の引数準備メソッド<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   * 第3引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> UpdateErrCase() {
    return Stream.of(
        Arguments.of(
            "料金IDがnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createUpdatePriceInfoRequestDto();
                  dto.setPriceId(null);
                  return dto;
                },
            "[1番目の料金IDに値が設定されていません。]"),
        Arguments.of(
            "料金IDが空文字の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createUpdatePriceInfoRequestDto();
                  dto.setPriceId("");
                  return dto;
                },
            "[1番目の料金IDに値が設定されていません。]"),
        Arguments.of(
            "料金IDがUUID以外の文字列の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createUpdatePriceInfoRequestDto();
                  dto.setPriceId("notUUID");
                  return dto;
                },
            "[1番目の料金IDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "リソースIDがnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createUpdatePriceInfoRequestDto();
                  dto.setResourceId(null);
                  return dto;
                },
            "[1番目のリソースIDに値が設定されていません。]"),
        Arguments.of(
            "リソースIDが空文字の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createUpdatePriceInfoRequestDto();
                  dto.setResourceId("");
                  return dto;
                },
            "[1番目のリソースIDに値が設定されていません。]"),
        Arguments.of(
            "オペレータIDがnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createUpdatePriceInfoRequestDto();
                  dto.setOperatorId(null);
                  return dto;
                },
            "[1番目のオペレータIDに値が設定されていません。]"),
        Arguments.of(
            "オペレータIDが空文字の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createUpdatePriceInfoRequestDto();
                  dto.setOperatorId(null);
                  return dto;
                },
            "[1番目のオペレータIDに値が設定されていません。]"));
  }

  /** validateForUpdate 正常系パラメタライズドテスト */
  @ParameterizedTest(name = "{0}")
  @MethodSource("UpdateNmlCase")
  public void testValidateForUpdate_nmlCase(String caseName, Supplier<PriceInfoRequestDto> sDto) {
    PriceInfoRequestDto dto = sDto.get();
    List<PriceInfoRequestDto> dtoList = new ArrayList<>();
    dtoList.add(dto);
    assertDoesNotThrow(() -> validator.validateAll(dtoList));
  }

  /** validateForUpdate 異常系パラメタライズドテスト */
  @ParameterizedTest(name = "{0}")
  @MethodSource("UpdateErrCase")
  public void testValidateForUpdate_errCase(
      String caseName, Supplier<PriceInfoRequestDto> sDto, String msg) {
    PriceInfoRequestDto dto = sDto.get();
    List<PriceInfoRequestDto> dtoList = new ArrayList<>();
    dtoList.add(dto);
    Exception ex =
        assertThrows(ValidationErrorException.class, () -> validator.validateAll(dtoList));
    assertEquals(msg, ex.getMessage());
  }

  /** データテンプレート ■登録更新リクエスト 登録更新削除_正常リクエストボディ */
  private static PriceInfoRequestDto createUpdatePriceInfoRequestDto() {
    PriceInfoRequestDto ret = new PriceInfoRequestDto();
    ret.setProcessingType(2);
    ret.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setResourceId("リソースID");
    ret.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
    ret.setPriceType(4);
    ret.setPricePerUnit(1);
    ret.setPrice(1000);
    ret.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret.setEffectiveEndTime("2025-11-13T11:00:00Z");
    ret.setPriority(1);
    ret.setOperatorId("ope01");
    ret.setRowNumber(1);

    return ret;
  }

  /**
   * メソッド名: validateForDelete<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   */
  static Stream<Arguments> DeleteNmlCase() {
    return Stream.of(
        Arguments.of(
            "テンプレート変更なし",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createDeletePriceInfoRequestDto();
                  return dto;
                }));
  }

  /**
   * メソッド名: validateForDelete<br>
   * ParameterizedTest用の引数準備メソッド<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   * 第3引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> DeleteErrCase() {
    return Stream.of(
        Arguments.of(
            "料金IDがnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createDeletePriceInfoRequestDto();
                  dto.setPriceId(null);
                  return dto;
                },
            "[1番目の料金IDに値が設定されていません。]"),
        Arguments.of(
            "料金IDが空文字の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createDeletePriceInfoRequestDto();
                  dto.setPriceId("");
                  return dto;
                },
            "[1番目の料金IDに値が設定されていません。]"),
        Arguments.of(
            "料金IDがUUID以外の文字列の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createDeletePriceInfoRequestDto();
                  dto.setPriceId("notUUID");
                  return dto;
                },
            "[1番目の料金IDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "オペレータIDがnullの場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createDeletePriceInfoRequestDto();
                  dto.setOperatorId(null);
                  return dto;
                },
            "[1番目のオペレータIDに値が設定されていません。]"),
        Arguments.of(
            "オペレータIDが空文字の場合",
            (Supplier<PriceInfoRequestDto>)
                () -> {
                  PriceInfoRequestDto dto = createDeletePriceInfoRequestDto();
                  dto.setOperatorId("");
                  return dto;
                },
            "[1番目のオペレータIDに値が設定されていません。]"));
  }

  /** validateForDelete 正常系パラメタライズドテスト */
  @ParameterizedTest(name = "{0}")
  @MethodSource("DeleteNmlCase")
  public void testValidateForDelete_nmlCase(String caseName, Supplier<PriceInfoRequestDto> sDto) {
    PriceInfoRequestDto dto = sDto.get();
    List<PriceInfoRequestDto> dtoList = new ArrayList<>();
    dtoList.add(dto);
    assertDoesNotThrow(() -> validator.validateAll(dtoList));
  }

  /** validateForDelete 異常系パラメタライズドテスト */
  @ParameterizedTest(name = "{0}")
  @MethodSource("DeleteErrCase")
  public void testValidateForDelete_errCase(
      String caseName, Supplier<PriceInfoRequestDto> sDto, String msg) {
    PriceInfoRequestDto dto = sDto.get();
    List<PriceInfoRequestDto> dtoList = new ArrayList<>();
    dtoList.add(dto);
    Exception ex =
        assertThrows(ValidationErrorException.class, () -> validator.validateAll(dtoList));
    assertEquals(msg, ex.getMessage());
  }

  /** データテンプレート ■登録更新削除リクエスト 登録更新削除_正常リクエストボディ */
  private static PriceInfoRequestDto createDeletePriceInfoRequestDto() {
    PriceInfoRequestDto ret = new PriceInfoRequestDto();
    ret.setProcessingType(3);
    ret.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setResourceId("リソースID");
    ret.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
    ret.setOperatorId("ope01");
    ret.setRowNumber(1);

    return ret;
  }

  /**
   * メソッド名: validateForGetList<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   */
  static Stream<Arguments> GetListNmlCase() {
    return Stream.of(
        Arguments.of(
            "テンプレート変更なし",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  return dto;
                }),
        Arguments.of(
            "必須以外null",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceId(null);
                  dto.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
                  dto.setPriceType(null);
                  dto.setPricePerUnitFrom(null);
                  dto.setPricePerUnitTo(null);
                  dto.setPriceFrom(null);
                  dto.setPriceTo(null);
                  dto.setEffectiveStartTime(null);
                  dto.setEffectiveEndTime(null);
                  dto.setSortOrders(null);
                  dto.setSortColumns(null);
                  return dto;
                }),
        Arguments.of(
            "必須以外空文字",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceId("");
                  dto.setEffectiveStartTime("");
                  dto.setEffectiveEndTime("");
                  dto.setSortOrders("");
                  dto.setSortColumns("");
                  return dto;
                }),
        Arguments.of(
            "リソース種別が10(離着陸場)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
                  return dto;
                }),
        Arguments.of(
            "リソース種別が20(機体)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceType(
                      BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT));
                  dto.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf22");
                  return dto;
                }),
        Arguments.of(
            "リソース種別が10(離着陸場)、リソースIDがUUID以外の文字列の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
                  dto.setResourceId("notUUID");
                  return dto;
                }),
        Arguments.of(
            "料金タイプが1の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(1));
                  return dto;
                }),
        Arguments.of(
            "料金タイプが2の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(2));
                  return dto;
                }),
        Arguments.of(
            "料金タイプが3の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(3));
                  return dto;
                }),
        Arguments.of(
            "料金タイプが4の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(4));
                  return dto;
                }),
        Arguments.of(
            "料金タイプが5の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(5));
                  return dto;
                }),
        Arguments.of(
            "料金タイプが6の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(6));
                  return dto;
                }),
        Arguments.of(
            "料金タイプが7の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(7));
                  return dto;
                }),
        Arguments.of(
            "料金単位(以上)が1の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPricePerUnitFrom(BigInteger.valueOf(1));
                  return dto;
                }),
        Arguments.of(
            "料金単位(以上)が2147483647の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPricePerUnitFrom(BigInteger.valueOf(2147483647));
                  return dto;
                }),
        Arguments.of(
            "料金単位(以下)が1の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPricePerUnitTo(BigInteger.valueOf(1));
                  return dto;
                }),
        Arguments.of(
            "料金単位(以下)が2147483647の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPricePerUnitTo(BigInteger.valueOf(2147483647));
                  return dto;
                }),
        Arguments.of(
            "料金(以上)が0の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceFrom(BigInteger.valueOf(0));
                  return dto;
                }),
        Arguments.of(
            "料金(以上)が2147483647の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceFrom(BigInteger.valueOf(2147483647));
                  return dto;
                }),
        Arguments.of(
            "料金(以下)が0の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceTo(BigInteger.valueOf(0));
                  return dto;
                }),
        Arguments.of(
            "料金(以下)が2147483647の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceTo(BigInteger.valueOf(2147483647));
                  return dto;
                }),
        Arguments.of(
            "ソート順が0、ソート対象列名がeffectiveTimeの場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setSortOrders("0");
                  dto.setSortColumns("effectiveTime");
                  return dto;
                }),
        Arguments.of(
            "ソート順が0,1、ソート対象列名がeffectiveTime,resourceTypeの場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setSortOrders("0,1");
                  dto.setSortColumns("effectiveTime,resourceType");
                  return dto;
                }),
        Arguments.of(
            "リソース種別がnull",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceType(null);
                  return dto;
                }));
  }

  /**
   * メソッド名: validateForGetList<br>
   * ParameterizedTest用の引数準備メソッド<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   * 第3引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> GetListErrCase() {
    return Stream.of(
        Arguments.of(
            "料金IDがUUID以外の文字列の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceId("notUUID");
                  return dto;
                },
            "[料金IDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "リソース種別が19(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceType(BigInteger.valueOf(19));
                  dto.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf22");
                  return dto;
                },
            "[リソース種別の値が不正です。\n範囲[20, 30]]"),
        Arguments.of(
            "リソース種別が31(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceType(BigInteger.valueOf(31));
                  dto.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf22");
                  return dto;
                },
            "[リソース種別の値が不正です。\n範囲[20, 30]]"),
        //
        Arguments.of(
            "リソース種別が21(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceType(BigInteger.valueOf(21));
                  dto.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf22");
                  return dto;
                },
            "[リソース種別の値が不正です。\n範囲[20, 30]]"),
        Arguments.of(
            "リソース種別が20(機体)、リソースIDがUUID以外の文字列の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceType(
                      BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT));
                  dto.setResourceId("notUUID");
                  return dto;
                },
            "[リソースIDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "料金タイプが0(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(0));
                  return dto;
                },
            "[料金タイプの値が不正です。\n範囲[1, 2, 3, 4, 5, 6, 7]]"),
        Arguments.of(
            "料金タイプが8(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(8));
                  return dto;
                },
            "[料金タイプの値が不正です。\n範囲[1, 2, 3, 4, 5, 6, 7]]"),
        Arguments.of(
            "料金タイプがInteger.MAX_VALUEを超える場合(オーバーフロー)",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(new BigInteger("4294967297")); // Integer.MAX_VALUE + 1 より大きい値
                  return dto;
                },
            "[料金タイプの値が不正です。\n範囲[1, 2, 3, 4, 5, 6, 7]]"),
        Arguments.of(
            "料金単位(以上)が0(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPricePerUnitFrom(BigInteger.valueOf(0));
                  return dto;
                },
            "[料金単位(以上)の値が不正です。\n最小値(1)、最大値(2,147,483,647)]"),
        Arguments.of(
            "料金単位(以下)が0(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPricePerUnitTo(BigInteger.valueOf(0));
                  return dto;
                },
            "[料金単位(以下)の値が不正です。\n最小値(1)、最大値(2,147,483,647)]"),
        Arguments.of(
            "料金(以上)が-1(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceFrom(BigInteger.valueOf(-1));
                  return dto;
                },
            "[料金(以上)の値が不正です。\n最小値(0)、最大値(2,147,483,647)]"),
        Arguments.of(
            "料金(以下)が-1(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceTo(BigInteger.valueOf(-1));
                  return dto;
                },
            "[料金(以下)の値が不正です。\n最小値(0)、最大値(2,147,483,647)]"),
        Arguments.of(
            "適用開始日時がフォーマット不正の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setEffectiveStartTime("2025/11/15T01:23:45Z");
                  return dto;
                },
            "[日時条件(開始)がサポートされていない形式です。\n入力値:2025/11/15T01:23:45Z]"),
        Arguments.of(
            "適用終了日時がフォーマット不正の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setEffectiveEndTime("2025/11/15T01:23:45Z");
                  return dto;
                },
            "[日時条件(終了)がサポートされていない形式です。\n入力値:2025/11/15T01:23:45Z]"),
        Arguments.of(
            "ソート順が2(値域外)、ソート対象列名がeffectiveTimeの場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setSortOrders("2");
                  dto.setSortColumns("effectiveTime");
                  return dto;
                },
            "[ソート順の値が不正です。\n範囲[0, 1]]"),
        Arguments.of(
            "ソート順が0、ソート対象列名がeffecteffectiveTime,resourceTypeiveTimeの場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setSortOrders("0");
                  dto.setSortColumns("effectiveTime,resourceType");
                  return dto;
                },
            "[ソート順とソート対象列の設定数が一致しません。]"),
        Arguments.of(
            "リソースIDがnullの場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceId(null);
                  return dto;
                },
            "[リソースIDに値が設定されていません。]"),
        Arguments.of(
            "リソースIDが空の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setResourceId("");
                  return dto;
                },
            "[リソースIDに値が設定されていません。]"),
        Arguments.of(
            "料金タイプが9999999999(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceType(BigInteger.valueOf(9999999999L));
                  return dto;
                },
            "[料金タイプの値が不正です。\n範囲[1, 2, 3, 4, 5, 6, 7]]"),
        Arguments.of(
            "料金単位(以上)が9999999999(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPricePerUnitFrom(BigInteger.valueOf(9999999999L));
                  return dto;
                },
            "[料金単位(以上)の値が不正です。\n最小値(1)、最大値(2,147,483,647)]"),
        Arguments.of(
            "料金単位(以下)が9999999999(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPricePerUnitTo(BigInteger.valueOf(9999999999L));
                  return dto;
                },
            "[料金単位(以下)の値が不正です。\n最小値(1)、最大値(2,147,483,647)]"),
        Arguments.of(
            "料金(以上)が9999999999(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceFrom(BigInteger.valueOf(-1));
                  return dto;
                },
            "[料金(以上)の値が不正です。\n最小値(0)、最大値(2,147,483,647)]"),
        Arguments.of(
            "料金(以下)が9999999999(値域外)の場合",
            (Supplier<PriceInfoSearchListRequestDto>)
                () -> {
                  PriceInfoSearchListRequestDto dto = createPriceInfoSearchListRequestDto();
                  dto.setPriceTo(BigInteger.valueOf(-1));
                  return dto;
                },
            "[料金(以下)の値が不正です。\n最小値(0)、最大値(2,147,483,647)]"));
  }

  /** validateForGetList 正常系パラメタライズドテスト */
  @ParameterizedTest(name = "{0}")
  @MethodSource("GetListNmlCase")
  public void testValidateForGetList_nmlCase(
      String caseName, Supplier<PriceInfoSearchListRequestDto> sDto) {
    PriceInfoSearchListRequestDto dto = sDto.get();
    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /** validateForGetList 異常系パラメタライズドテスト */
  @ParameterizedTest(name = "{0}")
  @MethodSource("GetListErrCase")
  public void testValidateForGetList_errCase(
      String caseName, Supplier<PriceInfoSearchListRequestDto> sDto, String msg) {
    PriceInfoSearchListRequestDto dto = sDto.get();
    Exception ex =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
    assertEquals(msg, ex.getMessage());
  }

  /** データテンプレート ■料金情報取得リクエスト 料金情報取得のリクエストボディ */
  private static PriceInfoSearchListRequestDto createPriceInfoSearchListRequestDto() {
    PriceInfoSearchListRequestDto ret = new PriceInfoSearchListRequestDto();
    ret.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setResourceId("リソースID");
    ret.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
    ret.setPriceType(BigInteger.valueOf(4));
    ret.setPricePerUnitFrom(BigInteger.valueOf(1));
    ret.setPricePerUnitTo(BigInteger.valueOf(2));
    ret.setPriceFrom(BigInteger.valueOf(1000));
    ret.setPriceTo(BigInteger.valueOf(2000));
    ret.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret.setEffectiveEndTime("2025-11-13T11:00:00Z");

    return ret;
  }
}
