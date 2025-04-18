# README

## インストール

このプロジェクトをローカル環境にインストールする手順を記載します。

1. git clone後

MQTTブローカの証明書を動作する環境にインポートします。

```bash
cd droneport-server

# プロジェクトのビルド
./gradlew build  

# プロジェクトをビルドして実行可能JARファイルを作成します。
./gradlew bootJar
```

## 環境変数

環境変数の説明と設定値例を記載します。

| 環境変数名                             | 物理名(docker)              | 環境変数名                                                                 | 説明                                           | 設定値例                             |
|----------------------------------------|-----------------------------|----------------------------------------------------------------------------|------------------------------------------------|--------------------------------------|
| データベースホスト                     | DATASOURCE_HOST             | --spring.datasource.url                                                    | DBの接続先です。接続先ホスト名指定してください | db                                   |
| MQTTブローカホスト                     | VIS_BROKER_HOST             | --systemsettings.settingclass.visConnectInfo.brokerUrl                     | MQTTの接続先です。URLのみ指定可能です          | ssl://mqtt.{接続先情報}:{ポート番号} |
| MQTTブローカユーザ名                   | VIS_BROKER_USER_NAME        | --systemsettings.settingclass.visConnectInfo.userName                      | MQTTのユーザ名です                             | admin                                |
| MQTTブローカパスワード                 | VIS_BROKER_PASSWORD         | --systemsettings.settingclass.visConnectInfo.password                      | MQTTのパスワードです                           | ー                                   |
| MQTTクライアントID                     | VIS_BROKER_CLIENT_ID        | --systemsettings.settingclass.visConnectInfo.clientId                      | MQTTのクライアントIDです                       | mqttDroneportServiceClientDc         |
| VISテレメトリ購読の有効化              | VIS_TELEMETRY_ENABLE        | --systemsettings.settingclass.visConnectInfo.enableSubscribeTelemetryInfo  | VISテレメトリ購読の有効化です                  | true                                 |
