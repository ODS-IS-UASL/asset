# 離着陸場及び機体リソース管理・提供モジュール

## 目次
- [概要](#概要)
- [技術スタック](#技術スタック)
- [リポジトリ構成](#リポジトリ構成)
- [環境構築手順](#環境構築手順)
  - [前提条件](#前提条件)
  - [1. 資材の配置](#1-資材の配置)
  - [2. 環境変数の設定](#2-環境変数の設定)
  - [3. ビルドおよびDockerイメージの作成](#3-ビルドおよびdockerイメージの作成)
  - [4. コンテナの起動](#4-コンテナの起動)
  - [5. データベースの初期化（初回のみ）](#5-データベースの初期化)
  - [6. 疎通確認](#6-疎通確認)
- [外部サービス](#外部サービス)
- [使用方法](#使用方法)
- [注意事項](#注意事項)
- [ライセンスおよび免責事項](#ライセンスおよび免責事項)

## 概要
本モジュールはドローン航路システムのサブシステムとして他のサブシステムと連携し、離着陸場・機体リソースの管理および提供を行うREST APIを提供します。

#### 提供するAPIの機能
以下の機能に関連するAPIを取り扱っています。
- 提供する離着陸場リソースの管理機能
- 提供する機体リソースおよび関連情報(ペイロード、飛行許可申請関連の情報)の管理機能
- 上記リソースの提供時の料金単価の管理機能
- 上記リソースの予約機能

詳細は同梱のOpenAPI仕様を参照願います。
- OpenAPI仕様(離着陸場関連API): [docs/OpenAPI_droneportInfo.yaml](docs/OpenAPI_droneportInfo.yaml)
- OpenAPI仕様(機体関連API): [docs/OpenAPI_aircraftInfo.yaml](docs/OpenAPI_aircraftInfo.yaml)
- OpenAPI仕様(料金関連API): [docs/OpenAPI_priceInfo.yaml](docs/OpenAPI_priceInfo.yaml)


## 技術スタック

#### アプリケーション構成（すべてDockerコンテナ内で動作）:
- Java: 17
- Spring Boot: 3.3.2
- Gradle: 8.9
- postgis/postgis:16-3.4
- PostGIS: 3.4.2

#### 動作確認済みの環境:
- Ubuntu 24.04.3 LTS（x86_64）
- Docker: 29.1.3

## リポジトリ構成
- ソースファイル(プロジェクトルート): [droneport-server/](droneport-server/)  
  本機能のソースファイル  
- OpenAPI仕様: [docs/](docs/)  
  本機能で取扱うAPIの仕様書  
- 環境構築関連: [setup/](setup/)  
  本機能を使用するための環境構築関連のファイル群:
- README: [README.md](README.md)  
  本ドキュメント  
- LICENSE: [LICENSE_hitachi](LICENSE_hitachi), [LICENSE_gridskyway](LICENSE_gridskyway)    
  ライセンスの記載

## 環境構築手順
### 前提条件
- Linux系環境での手順です
- 必要なコマンドはインストール済みであること
- 一部インターネット接続が必要なコマンドがあります
- 管理者権限ユーザーでの実行を前提とします

### 1. 資材の配置
本リポジトリの資材を任意のディレクトリ（以降 `project-root`）に配置してください。<br>
資材の取得方法（git clone、アーカイブ展開等）は問いません

```
project-root/
├── droneport-server/           # アプリケーションソースコード
│   ├── src/                    # ソースコード
│   ├── build.gradle            # Gradleビルド設定
│   └── settings.gradle         # Gradleプロジェクト設定
└── setup/                      # Docker・デプロイ関連
    ├── build_docker.sh         # ビルド・Dockerイメージ作成用スクリプト
    ├── Dockerfile              # Dockerイメージ定義ファイル
    ├── compose.yml             # コンテナ一括起動用Composeファイル
    ├── .env.example            # 環境変数ファイルの雛形
    └── droneport-db-init.sql   # DDL（初期DBスキーマ）
```


### 2. 環境変数の設定


.env.example を .env にコピーし、環境に合わせて編集してください。


```bash
cd <project-root>/setup

cp .env.example .env
vi .env
```

環境変数一覧：

| 論理名 | 環境変数名(物理名) | 説明 | 設定値例 | 必須 |
|------------|----------------|------|----------|------|
| データベースコンテナイメージ名 | DB_CONTAINER_IMAGE | データベースコンテナのイメージ | postgis/postgis:16-3.4 | 必須 |
| データベースコンテナ名 | DB_CONTAINER_NAME | データベースコンテナ名 | droneport-db | 必須 |
| DBポート | DB_PORT | DBコンテナのポート番号 | 5432 | 必須 |
| ドローンポートコンテナイメージ名 | DRONEPORT_CONTAINER_IMAGE | ドローンポートサービスのイメージ | droneport-server-local:latest | 必須 |
| ドローンポートコンテナ名 | DRONEPORT_CONTAINER_LABEL | ドローンポートサービスのラベル | droneport-app | 必須 |
| ドローンAPIポート | DRONEPORT_API_PORT | アプリケーションAPIのポート番号 | 8080 | 必須 |
| データベースホスト | DATASOURCE_HOST | DBの接続先 | droneport-db | 必須 |
| DB名 | DB_NAME | DB名 | droneroute | 必須 |
| DBユーザ名 | DB_USER_NAME | DBユーザ名 | droneroute | 必須 |
| DBパスワード | DB_PASSWORD | DBパスワード | droneroutepassword | 必須 |
| ユーザ属性取得APIオリジン | USER_ATTRIBUTE_API_ORIGIN | ユーザ属性取得APIのオリジン（プロトコル＋ホスト［＋ポート］。パスは含まない） | http://example.domain.com | 必須 |
| 航路運営事業者ID | ROUTE_OPERATOR_ID | 航路運営事業者IDです。（事業者ごとに固有） | 12345678-1234-1234-1234-123456789abc<br>※ダミー値です。実際の航路運営事業者IDに書き換えてください | 必須 |
| APIキー | ASSET_API_KEY | 平文APIキーをSHA-256でハッシュ化した値を設定します（salt は使用しません）。<br>リクエストのAPIキーを同様の方法で変換して一致チェックを行います<br>※ダミー値です。実際のAPIキーのハッシュ値に変更してください| 	8c284055dbb54b7f053a2dc612c3727c7aa36354361055f2110f4903ea8ee29c | 必須 |
| VISブローカホスト名 | VIS_BROKER_HOST | VIS機能のMQTTホスト名 | tcp://example.domain.com | 任意 |
| VISブローカユーザ名 | VIS_BROKER_USER_NAME | VIS機能のMQTTユーザ名 | visuser | 任意 |
| VISブローカパスワード | VIS_BROKER_PASSWORD | VIS機能のMQTTパスワード | vispassword | 任意 |
| VISブローカクライアントID | VIS_BROKER_CLIENT_ID | VIS機能のMQTTクライアントID | visclient | 任意 |
| VIS連携起動設定 | VIS_TELEMETRY_ENABLE | VIS機能のVISテレメトリ購読の有効化のフラグです | true | 必須 |

※VIS_TELEMETRY_ENABLE を true にする場合は、外部の MQTT ブローカサービスへの接続が必要となります。<br>
　VIS 機能を使用しない場合は false を設定してください（この場合、VIS 関連の環境変数は設定不要です）。


### 3. ビルドおよびDockerイメージの作成


ソースコードのビルドとDockerイメージの作成は `build_docker.sh` の実行のみで完結します。

#### ビルドおよびDockerイメージ作成
・実行コマンド
```bash
cd <project-root>/setup

bash build_docker.sh
```

・ビルド結果例
  - 成功時
    ```
    ====================================
    【OK】 Docker image build completed!

    Image: droneport-server-local:latest

    ====================================
    ```
  - 失敗時
    ```
    【NG】Docker image build failed!
    ```

#### イメージの確認
・実行コマンド
```bash
docker images
```
・イメージ確認結果例
```
IMAGE                           ID             DISK USAGE   CONTENT SIZE   EXTRA
droneport-server-local:latest   91a80e6d4582        503MB          154MB   
```

### 4. コンテナの起動


・実行コマンド
```bash
cd <project-root>/setup

docker compose up -d
```
・実行結果例
```
Image postgis/postgis:16-3.4 Pulled
Network droneroute.nw Created 
Container droneport-db Created
Container droneport-app Created
```
・確認コマンド
```bash
docker compose ps
```
・確認結果例
```
NAME            IMAGE                           COMMAND                  SERVICE     CREATED         STATUS         PORTS
droneport-app   droneport-server-local:latest   "java -jar app.jar"      droneport   2 minutes ago   Up 2 minutes   127.0.0.1:8080->8080/tcp
droneport-db    postgis/postgis:16-3.4          "docker-entrypoint.s…"   db          2 minutes ago   Up 2 minutes   127.0.0.1:5432->5432/tcp
```


### 5. データベースの初期化
初回セットアップ時のみ、下記コマンドでデータベースの初期スキーマを適用してください。

・実行コマンド
```bash
# データベース初期化
cd <project-root>/setup

docker exec -i droneport-db psql -U droneroute -d droneroute < droneport-db-init.sql
```

・実行結果（抜粋）
```
DROP TABLE
CREATE TABLE
ALTER TABLE
DROP SEQUENCE
CREATE SEQUENCE
...
（上記のような出力が表示されます。エラーがないことを確認してください）
```

### 6. 疎通確認

・疎通確認コマンド
```bash
curl http://localhost:8080/awshealth/check.html
```
・実行結果例
```
<!-- /var/www/html/health.html -->
<!DOCTYPE html>
<html>
<head>
<title>Health Check</title>
</head>
<body>
<h1>OK</h1>
</body>
</html>
```

※疎通確認用URL（/awshealth/check.html）は認可不要です。</br>
　業務APIは呼び出しの際に認可情報が必要です。</br>
　認可方式（APIキー / アクセストークン）はAPIごとに異なり、OpenAPI仕様書に記載しています。

上記の疎通確認ができればセットアップは完了です。

## 外部サービス

本モジュールの動作には関連リポジトリの以下の外部サービスが必要です。

### 認可関連サービス
- ユーザ属性取得API: ユーザの属性情報（権限・所属等）を取得します。

### 情報配信サービス
- MQTTブローカー: VISシステムとの連携に使用します。


## 使用方法
setupディレクトリに移動して以下を実施してください

・起動
```bash
docker compose up -d
```

・ログ確認（最新20行）
```bash
docker compose logs --tail=20 droneport
```

・ヘルスチェック
```bash
curl http://localhost:8080/awshealth/check.html
```

・停止
```bash
docker compose down
```

・再ビルド（ソース更新時）
```bash
bash build_docker.sh
docker compose down
docker compose up -d
```

## 注意事項
- 本リポジトリのソース等で使用している「droneport」という単語については機械式に限らない簡易離着陸場を含むドローンの離着陸場を意味しております。
- 本リポジトリ内で使用している「ペイロード」については、機体のオプションパーツ等の意で使用しております。(貨物や積載可能量の意ではない)

## ライセンスおよび免責事項
- 本リポジトリはMITライセンスで提供されています。
- 詳細は同梱の[LICENSE_hitachi](LICENSE_hitachi), [LICENSE_gridskyway](LICENSE_gridskyway) を参照してください。
- 本リポジトリの内容は予告なしに変更および削除を行うことがあります。
