#!/bin/bash

# 启动微服务 (gateway, auth, user, content, notify, file)

set -euo pipefail

# 脚本所在目录的父目录（项目根目录）
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

cd "$PROJECT_ROOT"

echo "=========================================="
echo "启动微服务"
echo "=========================================="
echo ""

# 检查 docker-compose.service.yml 文件是否存在
if [ ! -f "docker-compose.service.yml" ]; then
    echo "错误: 找不到 docker-compose.service.yml 文件"
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

# 检查环境服务是否运行
echo "检查环境服务状态..."
required_services=("mysql" "redis" "rabbitmq" "nacos")
all_running=true

for service in "${required_services[@]}"; do
    if ! docker ps --filter "name=$service" --filter "status=running" | grep -q "$service"; then
        echo "警告: $service 未运行"
        all_running=false
    fi
done

if [ "$all_running" = false ]; then
    echo ""
    echo "警告: 部分环境服务未运行，建议先运行 ./scripts/start_env.sh"
    read -p "是否继续启动微服务？[y/N] " confirm
    if [[ ! "$confirm" =~ ^[Yy]$ ]]; then
        echo "操作已取消"
        exit 0
    fi
fi

echo ""
echo "正在启动微服务..."
echo "注意: 微服务会按照依赖顺序启动，这可能需要一些时间"
echo ""

# 启动微服务
$COMPOSE_CMD -f docker-compose.service.yml up -d

echo ""
echo "=========================================="
echo "微服务启动完成！"
echo "=========================================="
echo ""

# 显示容器状态
echo "容器状态："
docker ps --filter "name=-service" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "提示："
echo "  - 网关服务 (gateway-service):  内部端口 8080"
echo "  - 认证服务 (auth-service):     内部端口 8090"
echo "  - 用户服务 (user-service):     内部端口 8120"
echo "  - 内容服务 (content-service):  内部端口 8150"
echo "  - 基础服务 (infra-service):   内部端口 8110"
echo ""
echo "查看日志："
echo "  docker-compose -f docker-compose.service.yml logs -f [服务名]"
echo ""