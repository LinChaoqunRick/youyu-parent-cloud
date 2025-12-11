#!/bin/bash

# 删除所有以 -service 结尾的 Docker 容器

set -euo pipefail

echo "正在查找名称以 '-service' 结尾的容器..."

# 获取所有匹配的容器ID
container_ids=$(docker ps -a --filter "name=-service$" --format "{{.ID}}")

if [ -z "$container_ids" ]; then
    echo "没有找到以 '-service' 结尾的容器"
    exit 0
fi

echo "找到以下容器:"
docker ps -a --filter "name=-service$" --format "table {{.ID}}\t{{.Names}}\t{{.Status}}"

read -p "确定要删除这些容器吗？[y/N] " confirm
if [[ "$confirm" =~ ^[Yy]$ ]]; then
    echo "正在停止运行中的容器..."
    docker stop $container_ids 2>/dev/null || true

    echo "正在删除容器..."
    docker rm $container_ids

    echo "成功删除以下容器:"
    echo "$container_ids"
else
    echo "操作已取消"
    exit 0
fi