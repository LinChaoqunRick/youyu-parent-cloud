#!/bin/bash

# 删除所有以 -service 结尾的容器和以 services-code- 开头的镜像

set -euo pipefail

echo "===== 容器清理 ====="

# 1. 删除以 -service 结尾的容器
echo "正在查找名称以 '-service' 结尾的容器..."

container_ids=$(docker ps -a --filter "name=-service$" --format "{{.ID}}")

if [ -z "$container_ids" ]; then
    echo "没有找到以 '-service' 结尾的容器"
else
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
        echo "容器删除操作已取消"
    fi
fi

echo -e "\n===== 镜像清理 ====="

# 2. 删除以 services-code- 开头的镜像
echo "正在查找名称以 'services-code-' 开头的镜像..."

image_ids=$(docker images --filter "reference=services-code-*" --format "{{.ID}}")

if [ -z "$image_ids" ]; then
    echo "没有找到以 'services-code-' 开头的镜像"
else
    echo "找到以下镜像:"
    docker images --filter "reference=services-code-*" --format "table {{.ID}}\t{{.Repository}}\t{{.Tag}}"

    read -p "确定要删除这些镜像吗？[y/N] " confirm
    if [[ "$confirm" =~ ^[Yy]$ ]]; then
        echo "正在删除镜像..."
        docker rmi $image_ids

        echo "成功删除以下镜像:"
        echo "$image_ids"
    else
        echo "镜像删除操作已取消"
    fi
fi

echo -e "\n===== 清理完成 ====="