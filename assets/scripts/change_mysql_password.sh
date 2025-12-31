#!/bin/bash

CONTAINER_NAME="mysql"
MYSQL_USER="root"
OLD_PASSWORD="$1"
NEW_PASSWORD="$2"

if [ -z "$OLD_PASSWORD" ] || [ -z "$NEW_PASSWORD" ]; then
  echo "用法: $0 <旧密码> <新密码>"
  exit 1
fi

docker exec -i ${CONTAINER_NAME} mysql \
  -u${MYSQL_USER} -p${OLD_PASSWORD} <<EOF
ALTER USER '${MYSQL_USER}'@'%' IDENTIFIED BY '${NEW_PASSWORD}';
ALTER USER '${MYSQL_USER}'@'localhost' IDENTIFIED BY '${NEW_PASSWORD}';
FLUSH PRIVILEGES;
EOF

if [ $? -eq 0 ]; then
  echo "✅ MySQL 密码修改成功"
else
  echo "❌ 密码修改失败，请检查旧密码是否正确"
fi
