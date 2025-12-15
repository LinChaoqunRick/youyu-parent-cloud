#!/bin/bash

set -e

JENKINS_CONTAINER_NAME=jenkins
JENKINS_IMAGE=jenkins/jenkins:lts
JENKINS_HOME=/data/volumes/jenkins

echo "==============================="
echo " Jenkins Docker 安装脚本"
echo "==============================="

# 1. 检查 Docker
if ! command -v docker &> /dev/null; then
  echo "❌ Docker 未安装，请先安装 Docker"
  exit 1
fi

echo "✅ Docker 已安装"

# 2. 创建 Jenkins 数据目录
if [ ! -d "$JENKINS_HOME" ]; then
  echo "📁 创建 Jenkins 数据目录: $JENKINS_HOME"
  mkdir -p "$JENKINS_HOME"
fi

# 3. 设置权限（避免 Jenkins 启动失败）
echo "🔐 设置目录权限"
chmod -R 777 "$JENKINS_HOME"

# 4. 如果已存在 Jenkins 容器，先停止并删除
if docker ps -a --format '{{.Names}}' | grep -w "$JENKINS_CONTAINER_NAME" > /dev/null; then
  echo "⚠ 发现已有 Jenkins 容器，正在移除"
  docker stop "$JENKINS_CONTAINER_NAME" || true
  docker rm "$JENKINS_CONTAINER_NAME" || true
fi

# 5. 拉取 Jenkins 镜像
echo "📦 拉取 Jenkins 镜像: $JENKINS_IMAGE"
docker pull "$JENKINS_IMAGE"

# 6. 启动 Jenkins
echo "🚀 启动 Jenkins 容器"
docker run -d \
  --name "$JENKINS_CONTAINER_NAME" \
  -p 8080:8080 \
  -p 50000:50000 \
  -v "$JENKINS_HOME:/var/jenkins_home" \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --restart unless-stopped \
  "$JENKINS_IMAGE"

echo "==============================="
echo "🎉 Jenkins 启动完成"
echo "==============================="
echo "访问地址: http://<服务器IP>:8080"
echo
echo "初始管理员密码查看命令："
echo "docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword"