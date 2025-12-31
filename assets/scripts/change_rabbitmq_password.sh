#!/bin/bash

CONTAINER_NAME="rabbitmq"
RABBIT_USER="$1"
NEW_PASSWORD="$2"

if [ -z "$RABBIT_USER" ] || [ -z "$NEW_PASSWORD" ]; then
  echo "用法: $0 <用户名> <新密码>"
  echo "示例: $0 admin newpassword"
  exit 1
fi

docker exec -it ${CONTAINER_NAME} rabbitmqctl change_password \
  ${RABBIT_USER} ${NEW_PASSWORD}

if [ $? -eq 0 ]; then
  echo "✅ RabbitMQ 用户 ${RABBIT_USER} 密码修改成功"
else
  echo "❌ RabbitMQ 密码修改失败"
fi