#!/bin/bash

# 启动环境服务 (MySQL, Redis, RabbitMQ, Nacos)

set -euo pipefail

# 脚本所在目录的上上级目录（项目根目录）
PROJECT_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"

cd "$PROJECT_ROOT"

echo "=========================================="
echo "启动环境服务"
echo "=========================================="
echo ""

# 检查 docker-compose.env.yml 文件是否存在
if [ ! -f "docker-compose.env.yml" ]; then
    echo "错误: 找不到 docker-compose.env.yml 文件"
    exit 1
fi

# 检测使用 docker-compose 还是 docker compose
if command -v docker-compose &> /dev/null; then
    COMPOSE_CMD="docker-compose --compatibility"
    echo "使用 docker-compose 命令"
elif docker compose version &> /dev/null 2>&1; then
    COMPOSE_CMD="docker compose"
    echo "使用 docker compose 命令"
else
    echo "错误: 未找到 docker-compose 或 docker compose 命令"
    exit 1
fi

echo ""
echo "正在启动环境服务..."
echo ""

# 启动环境服务
$COMPOSE_CMD -f docker-compose.env.yml up -d

echo ""
echo "=========================================="
echo "环境服务启动完成！"
echo "=========================================="
echo ""

# 显示容器状态
echo "容器状态："
docker ps --filter "name=mysql" --filter "name=redis" --filter "name=rabbitmq" --filter "name=nacos" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "提示："
echo "  - MySQL:    localhost:3306"
echo "  - Redis:    localhost:6379"
echo "  - RabbitMQ: localhost:5672 (管理界面: http://localhost:15672)"
echo "  - Nacos:    http://localhost:8848/nacos"
echo ""