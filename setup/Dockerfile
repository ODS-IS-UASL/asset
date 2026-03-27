ARG TARGET_APP=droneport-server
ARG VERSION=0.0.1

# ビルドステージ
FROM gradle:8.9-jdk17-alpine AS builder
ARG TARGET_APP
ARG VERSION
COPY --chown=gradle:gradle ./droneport-server /home/gradle/droneport-server

WORKDIR /home/gradle/droneport-server

# Gradle bootJarでjarファイルを作成
RUN gradle bootJar --no-daemon

# 実行ステージ
FROM eclipse-temurin:17-jre-jammy 
ARG TARGET_APP
ARG VERSION

# 作業ディレクトリ
WORKDIR /app
# ビルドステージからSpring Bootアプリケーションのjarファイルをコピー
COPY --from=builder /home/gradle/droneport-server/build/libs/${TARGET_APP}-${VERSION}.jar app.jar
# ポートを公開 (Spring Bootのデフォルトポート)
EXPOSE 8080

# アプリケーションの実行コマンド
ENTRYPOINT ["java","-jar","app.jar"]
