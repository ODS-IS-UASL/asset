#!/bin/bash

# ====================================================================
# Dockerイメージビルドスクリプト
# ====================================================================
# このスクリプトは、Dockerコンテナ内でJARファイルをビルドし、
# Dockerイメージを作成します。
#
# 前提条件:
# - envファイルが存在すること
# - Dockerがインストールされていること
# ====================================================================

# ====================================================================
# 環境変数の読み込み
# ====================================================================
ENV_FILE="./.env"

if [ ! -f "$ENV_FILE" ]; then
    echo "✗ Error: Environment file not found: $ENV_FILE"
    exit 1
fi

echo "Loading environment variables from $ENV_FILE..."
set -a  # 以降の変数を自動的にエクスポート
source "$ENV_FILE"
set +a

# ====================================================================
# ビルド用変数設定
# ====================================================================
APP_VERSION=0.0.1                       # アプリケーションバージョン
TARGET_APP=droneport-server             # アプリケーション名
IMAGE_TAG=${DRONEPORT_CONTAINER_IMAGE:-droneport-server-local:latest}  # envファイルから読み込み、未定義ならローカル用デフォルト値

# ====================================================================
# Dockerイメージビルド（jar作成を含む）
# ====================================================================
echo ""
echo "===================================="
echo "Building Docker image..."
echo "Target: ${IMAGE_TAG}"
echo "Version: ${APP_VERSION}"
echo "===================================="
echo ""
sudo docker build \
    --progress=plain \
    --build-arg TARGET_APP=$TARGET_APP \
    --build-arg VERSION=$APP_VERSION \
    -f ./Dockerfile \
    ../ \
    --tag ${IMAGE_TAG}

# ビルド結果を確認
if [ $? -eq 0 ]; then
    echo ""
    echo "===================================="
    echo "【OK】 Docker image build completed!"
    echo "Image: ${IMAGE_TAG}"
    echo "===================================="
else
    echo "【NG】 Docker image build failed!"
    exit 1
fi

