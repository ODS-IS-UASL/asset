package com.hitachi.droneroute.cmn.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hitachi.droneroute.cmn.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** DroneRouteControllerAdviceクラスの単体テスト */
public class DroneRouteControllerAdviceTest {

  /**
   * メソッド名: handleAppErrorException<br>
   * 試験名: AppErrorExceptionのハンドリングが正しく行われる<br>
   * 条件: 例外を渡す<br>
   * 結果: BAD_REQUESTステータスとエラーメッセージを含むレスポンスが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testHandleAppErrorException() {
    DroneRouteControllerAdvice conAdvice = new DroneRouteControllerAdvice();
    Exception e = new Exception("testErr");

    ResponseEntity<ErrorResponse> res =
        new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    ResponseEntity<ErrorResponse> result = conAdvice.handleAppErrorException(e);
    assertEquals(res.getStatusCode(), result.getStatusCode());
    assertEquals(res.getBody().getErrorDetail(), result.getBody().getErrorDetail());
  }

  /**
   * メソッド名: handleNotFoundException<br>
   * 試験名: NotFoundExceptionのハンドリングが正しく行われる<br>
   * 条件: 例外を渡す<br>
   * 結果: BAD_REQUESTステータスとエラーメッセージを含むレスポンスが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testHandleNotFoundException() {
    DroneRouteControllerAdvice conAdvice = new DroneRouteControllerAdvice();
    Exception e = new Exception("testErr");

    ResponseEntity<ErrorResponse> res =
        new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    ResponseEntity<ErrorResponse> result = conAdvice.handleNotFoundException(e);
    assertEquals(res.getStatusCode(), result.getStatusCode());
    assertEquals(res.getBody().getErrorDetail(), result.getBody().getErrorDetail());
  }

  /**
   * メソッド名: handleValidationErrorException<br>
   * 試験名: ValidationErrorExceptionのハンドリングが正しく行われる<br>
   * 条件: 例外を渡す<br>
   * 結果: BAD_REQUESTステータスとエラーメッセージを含むレスポンスが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testHandleValidationErrorException() {
    DroneRouteControllerAdvice conAdvice = new DroneRouteControllerAdvice();
    Exception e = new Exception("testErr");

    ResponseEntity<ErrorResponse> res =
        new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    ResponseEntity<ErrorResponse> result = conAdvice.handleValidationErrorException(e);
    assertEquals(res.getStatusCode(), result.getStatusCode());
    assertEquals(res.getBody().getErrorDetail(), result.getBody().getErrorDetail());
  }

  /**
   * メソッド名: handleServiceException<br>
   * 試験名: ServiceExceptionのハンドリングが正しく行われる<br>
   * 条件: 例外を渡す<br>
   * 結果: INTERNAL_SERVER_ERRORステータスとエラーメッセージを含むレスポンスが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testHandleServiceException() {
    DroneRouteControllerAdvice conAdvice = new DroneRouteControllerAdvice();
    Exception e = new Exception("testErr");

    ResponseEntity<ErrorResponse> res =
        new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    ResponseEntity<ErrorResponse> result = conAdvice.handleServiceException(e);
    assertEquals(res.getStatusCode(), result.getStatusCode());
    assertEquals(res.getBody().getErrorDetail(), result.getBody().getErrorDetail());
  }

  /**
   * メソッド名: handleHttpMessageNotReadableException<br>
   * 試験名: HttpMessageNotReadableExceptionのハンドリングが正しく行われる<br>
   * 条件: 例外を渡す<br>
   * 結果: BAD_REQUESTステータスとエラーメッセージを含むレスポンスが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testHandleHttpMessageNotReadableException() {
    DroneRouteControllerAdvice conAdvice = new DroneRouteControllerAdvice();
    Exception e = new Exception("testErr");

    ResponseEntity<ErrorResponse> res =
        new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    ResponseEntity<ErrorResponse> result = conAdvice.handleHttpMessageNotReadableException(e);
    assertEquals(res.getStatusCode(), result.getStatusCode());
    assertEquals(res.getBody().getErrorDetail(), result.getBody().getErrorDetail());
  }

  /**
   * メソッド名: handleOtherException<br>
   * 試験名: その他の例外のハンドリングが正しく行われる<br>
   * 条件: 例外を渡す<br>
   * 結果: INTERNAL_SERVER_ERRORステータスとエラーメッセージを含むレスポンスが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testHandleOtherException() {
    DroneRouteControllerAdvice conAdvice = new DroneRouteControllerAdvice();
    Exception e = new Exception("testErr");

    ResponseEntity<ErrorResponse> res =
        new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    ResponseEntity<ErrorResponse> result = conAdvice.handleOtherException(e);
    assertEquals(res.getStatusCode(), result.getStatusCode());
    assertEquals(res.getBody().getErrorDetail(), result.getBody().getErrorDetail());
  }
}
