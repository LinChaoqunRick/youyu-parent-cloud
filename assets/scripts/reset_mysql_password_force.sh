#!/bin/bash

CONTAINER_NAME="mysql"
NEW_PASSWORD="$1"

if [ -z "$NEW_PASSWORD" ]; then
  echo "用法: $0 <新密码>"
  exit 1
fi

echo "🛑 停止 MySQL 容器..."
docker stop ${CONTAINER_NAME}

echo "🚀 以跳过权限模式启动..."
docker run --rm \
  --name mysql-reset-temp \
  --network container:${CONTAINER_NAME} \
  -v $(docker inspect ${CONTAINER_NAME} --format='{{range .Mounts}}{{.Source}}:{{.Destination}} {{end}}') \
  $(docker inspect ${CONTAINER_NAME} --format='{{.Config.Image}}') \
  mysqld --skip-grant-tables --skip-networking &
sleep 8

echo "🔑 重置密码..."
docker exec -i mysql-reset-temp mysql <<EOF
ALTER USER 'root'@'%' IDENTIFIED BY '${NEW_PASSWORD}';
ALTER USER 'root'@'localhost' IDENTIFIED BY '${NEW_PASSWORD}';
FLUSH PRIVILEGES;
EOF

echo "🧹 清理临时容器..."
docker stop mysql-reset-temp

echo "🔄 重启原 MySQL 容器..."
docker start ${CONTAINER_NAME}

echo "✅ root 密码已重置"