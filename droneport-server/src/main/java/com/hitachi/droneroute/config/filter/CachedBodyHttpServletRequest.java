package com.hitachi.droneroute.config.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/** リクエストボディを複数回読み込み可能にするHttpServletRequestラッパー */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

  private final byte[] cachedBody;

  /**
   * リクエストのInputStreamを読み込んでキャッシュするコンストラクタ
   *
   * @param request キャッシュする元のHttpServletRequest
   * @throws IOException 入出力エラーが発生した場合にスローされる例外
   */
  public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
    super(request);
    this.cachedBody = request.getInputStream().readAllBytes();
  }

  /**
   * キャッシュされたリクエストボディを取得するメソッド
   *
   * @return キャッシュされたリクエストボディのバイト配列
   */
  public byte[] getCachedBody() {
    return cachedBody;
  }

  @Override
  /**
   * キャッシュされたリクエストボディを取得するServletInputStreamを返すメソッド
   *
   * @return キャッシュされたリクエストボディを読み込むServletInputStream
   */
  public ServletInputStream getInputStream() {
    return new CachedBodyServletInputStream(this.cachedBody);
  }

  @Override
  /**
   * キャッシュされたリクエストボディを読み込むBufferedReaderを返すメソッド
   *
   * @return キャッシュされたリクエストボディを読み込むBufferedReader
   */
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.cachedBody)));
  }

  /** ServletInputStreamのラッパークラス */
  private static class CachedBodyServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream inputStream;

    /**
     * コンストラクタ。キャッシュされたリクエストボディを受け取り、ByteArrayInputStreamを初期化する。
     *
     * @param cachedBody キャッシュされたリクエストボディのバイト配列
     */
    CachedBodyServletInputStream(byte[] cachedBody) {
      this.inputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    /**
     * リクエストボディの読み取りが完了しているかを判定するメソッド
     *
     * @return trueの場合は読み取りが完了している
     */
    public boolean isFinished() {
      return inputStream.available() == 0;
    }

    @Override
    /**
     * リクエストボディの読み取りが可能かを判定するメソッド
     *
     * @return 実装必須なだけで現状では用途なしのため、true固定
     */
    public boolean isReady() {
      return true;
    }

    @Override
    /**
     * ReadListenerを設定するメソッド 実装必須なだけで現状では用途なしのため、呼び出し時には例外をスロー
     *
     * @param readListener 読み込みリスナー
     * @throws UnsupportedOperationException このメソッドは現在サポートされていません
     */
    public void setReadListener(ReadListener readListener) {
      throw new UnsupportedOperationException();
    }

    @Override
    /**
     * リクエストボディから1バイト読み取るメソッド
     *
     * @return 読み取ったバイトの値、またはストリームの終わりに達した場合は-1
     */
    public int read() {
      return inputStream.read();
    }
  }
}
