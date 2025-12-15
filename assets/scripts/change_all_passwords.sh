#!/bin/bash

MYSQL_OLD="$1"
MYSQL_NEW="$2"
REDIS_OLD="$3"
REDIS_NEW="$4"
RABBIT_USER="$5"
RABBIT_NEW="$6"

./change_mysql_password.sh ${MYSQL_OLD} ${MYSQL_NEW}
./change_redis_password.sh ${REDIS_OLD} ${REDIS_NEW}
./change_rabbitmq_password.sh ${RABBIT_USER} ${RABBIT_NEW}