#!/bin/bash

CONTAINER_NAME="redis"
OLD_PASSWORD="$1"
NEW_PASSWORD="$2"

if [ -z "$NEW_PASSWORD" ]; then
  echo "用法: $0 [旧密码] <新密码>"
  echo "示例:"
  echo "  $0 oldpass newpass"
  echo "  $0 '' newpass   # 原本无密码"
  exit 1
fi

if [ -z "$OLD_PASSWORD" ]; then
  AUTH_CMD=""
else
  AUTH_CMD="-a ${OLD_PASSWORD}"
fi

docker exec -i ${CONTAINER_NAME} redis-cli ${AUTH_CMD} <<EOF
CONFIG SET requirepass ${NEW_PASSWORD}
CONFIG REWRITE
EOF

if [ $? -eq 0 ]; then
  echo "✅ Redis 密码修改成功"
else
  echo "❌ Redis 密码修改失败（请确认旧密码是否正确）"
fi