/*
 Navicat Premium Data Transfer

 Source Server         : 101.43.6.194
 Source Server Type    : MySQL
 Source Server Version : 50730 (5.7.30)
 Source Host           : 101.43.6.194:3306
 Source Schema         : nacos

 Target Server Type    : MySQL
 Target Server Version : 50730 (5.7.30)
 File Encoding         : 65001

 Date: 07/04/2025 15:12:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                               `group_id` varchar(128) COLLATE utf8_bin DEFAULT NULL,
                               `content` longtext COLLATE utf8_bin NOT NULL COMMENT 'content',
                               `md5` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'md5',
                               `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                               `src_user` text COLLATE utf8_bin COMMENT 'source user',
                               `src_ip` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'source ip',
                               `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL,
                               `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '租户字段',
                               `c_desc` varchar(256) COLLATE utf8_bin DEFAULT NULL,
                               `c_use` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `effect` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `type` varchar(64) COLLATE utf8_bin DEFAULT NULL,
                               `c_schema` text COLLATE utf8_bin,
                               `encrypted_data_key` text COLLATE utf8_bin NOT NULL COMMENT '秘钥',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

-- ----------------------------
-- Records of config_info
-- ----------------------------
BEGIN;
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (76, 'gateway-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\r\n  servlet:\r\n    multipart:\r\n      max-file-size: 2MB\r\n      max-request-size: 5MB\r\n  datasource:\r\n    driver-class-name: com.mysql.cj.jdbc.Driver\r\n    url: jdbc:mysql://mysql:3306/youyu?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8\r\n    username: root\r\n    password: youyu1233\r\n    hikari:\r\n      minimum-idle: 3\r\n      maximum-pool-size: 20\r\n      max-lifetime: 30000 # 不能小于30秒，否则默认回到1800秒\r\n      connection-test-query: SELECT 1\r\n  redis:\r\n    host: redis\r\n    port: 6379\r\n    database: 0 #操作的是0号数据库\r\n    timeout: 60s\r\n    lettuce:\r\n      shutdown-timeout: 100\r\n      # pool:\r\n      #   max-idle: 30\r\n      #   max-active: 8\r\n      #   max-wait: 10000\r\n      #   min-idle: 10\r\nmybatis-plus:\r\n  mapper-locations: classpath*:/mapper/**/*.xml\r\n  configuration:\r\n    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl\r\n    # log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\r\n  global-config:\r\n    db-config:\r\n      logic-delete-field: deleted\r\n      logic-delete-value: 1\r\n      logic-not-delete-value: 0\r\n      id-type: auto\r\n    banner: off # 关闭mybatisplus启动图标\r\npagehelper:\r\n  helperDialect: mysql\r\n  supportMethodsArguments: true\r\n  params: count=countSql\r\n  page-size-zero: true\r\nlogging:\r\n  level:\r\n    com.youyu.mapper: debug\r\n', '3f9954f4fb16617ff473c120f71a108a', '2024-01-06 12:30:03', '2024-01-06 12:30:03', NULL, '0:0:0:0:0:0:0:1', '', '68f255ef-8eef-4f9f-af90-ab56e7e93d4b', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (77, 'gateway-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug\n', '1e3b549a089f1fcb2fff9ef7f4bbf6ff', '2024-01-06 12:40:41', '2025-04-07 15:06:57', 'nacos', '27.154.213.153', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (78, 'auth-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug\nconnect:\n  qq:\n    appID: 102083273\n    appKey: sgxxxxxxx\n    scope: get_user_info,add_topic,add_one_blog,add_album,upload_pic,list_album,add_share,check_page_fans,add_t,add_pic_t,del_t,get_repost_list,get_info,get_other_info,get_fanslist,get_idollist,add_idol,del_ido,get_tenpay_addr\n    authorizeURL: https://graph.qq.com/oauth2.0/authorize\n    redirectURI: https://v2.youyul.com\n    accessTokenURL: https://graph.qq.com/oauth2.0/token\n    openIDURL: https://graph.qq.com/oauth2.0/me\n    userInfoURL: https://graph.qq.com/user/get_user_info\n  alipay:\n    clientId: xxx\n    clientSecret: xxx\n  github:\n    clientId: xxx\n    clientSecret: xxx\n    redirectURI: http://192.168.147.1:3000/githubConnect\n    authorizeURL: https://github.com/login/oauth/authorize\n    accessTokenURL: https://github.com/login/oauth/access_token\n    userInfoURL: https://api.github.com/user\n    ', '228496c58fbc41aecb22336182a1565a', '2024-01-06 12:43:52', '2025-04-07 15:09:01', 'nacos', '27.154.213.153', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (79, 'user-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug\namap:\n  key: xxxxxxx', 'd14f6ba86d3a5b1462cc81b44bb9caab', '2024-01-06 13:08:12', '2025-04-07 15:09:20', 'nacos', '27.154.213.153', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (80, 'post-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug\ncolumn:\n  columnMaxNum: 10\nfavorites:\n  favoritesMaxNum: 10', 'b9f24710c769de71629abcabe6dd7e48', '2024-01-06 13:09:00', '2024-12-11 11:02:52', 'nacos', '110.87.119.197', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (81, 'moment-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug', '11bf81397c8ef0aa0439be5a8751f04c', '2024-01-06 13:09:42', '2024-12-11 11:03:22', 'nacos', '110.87.119.197', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (82, 'note-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug', '11bf81397c8ef0aa0439be5a8751f04c', '2024-01-06 13:10:17', '2024-12-11 11:03:42', 'nacos', '110.87.119.197', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (84, 'mail-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\n  mail:\n    host: smtp.163.com #发送邮件服务器\n    username: xxx@163.com #网易邮箱\n    password: xxxxx #客户端授权码\n    protocol: smtp #发送邮件协议\n    properties.mail.smtp.auth: true\n    properties.mail.smtp.port: 465 #465或者994\n    nickname: 有语\n    from: harunayouyu@163.com  #发送邮件的地址，和上面username一致\n    #    properties.mail.display.sendmail: 有语 #可以任意\n    #    properties.mail.display.sendname: 有语 #可以任意\n    properties.mail.smtp.starttls.enable: true\n    properties.mail.smtp.starttls.required: true\n    properties.mail.smtp.ssl.enable: true #开启SSL\n    default-encoding: utf-8\nlogging:\n  level:\n    com.youyu.mapper: debug', '5e44a57dcb2e1db40f41a030a619f91c', '2024-01-06 13:11:41', '2025-04-07 15:09:44', 'nacos', '27.154.213.153', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (86, 'test-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug', '11bf81397c8ef0aa0439be5a8751f04c', '2024-01-06 13:12:41', '2024-12-11 11:04:36', 'nacos', '110.87.119.197', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (91, 'sms-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug\naliyun:\n  oss:\n    accessKeyId: xxx # 访问身份验证中用到用户标识\n    accessKeySecret: xxx # 用户用于加密签名字符串和oss用来验证签名字符串的密钥', '999ed4e798482b406d9c211ecd967900', '2024-02-19 15:07:39', '2025-04-07 15:10:01', 'nacos', '27.154.213.153', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (99, 'file-service-prod.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug\naliyun:\n  oss:\n    endpoint: xxxx # oss对外服务的访问域名\n    accessKeyId: xxx # 访问身份验证中用到用户标识\n    accessKeySecret: xxx # 用户用于加密签名字符串和oss用来验证签名字符串的密钥\n    bucket: xxx # oss的存储空间\n    roleArn: xxx\n    host: xxx\n    endpointRAM: xxx\n    accessKeyIdRAM: xxx\n    accessKeySecretRAM: xxx\n    policy:\n      expire: 300 # 签名有效期(S)\n      maxSize: 10 # 上传文件大小(M)', '103bc005ca1c6e764b5bd33704733537', '2024-03-18 21:53:39', '2025-04-07 15:10:36', 'nacos', '27.154.213.153', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (109, 'album-service.yaml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      max-file-size: 2MB\n      max-request-size: 5MB\nlogging:\n  level:\n    com.youyu.mapper: debug\naliyun:\n  oss:\n    endpoint: xxx # oss对外服务的访问域名\n    accessKeyId: xxx # 访问身份验证中用到用户标识\n    accessKeySecret: xxx # 用户用于加密签名字符串和oss用来验证签名字符串的密钥\n    bucket: youyu-album # oss的存储空间\n    host: https://youyu-album.oss-cn-beijing.aliyuncs.com\n    policy:\n      expire: 300 # 签名有效期(S)\n      maxSize: 11 # 上传文件大小(M)', 'bfd92ae755a469083009e725d903eed2', '2024-06-03 13:42:36', '2025-04-07 15:11:02', 'nacos', '27.154.213.153', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (111, 'database.yaml', 'DEFAULT_GROUP', 'spring:  \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://mysql:3306/youyu?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8\n    username: root\n    password: xxx\n    hikari:\n      minimum-idle: 3\n      maximum-pool-size: 20\n      max-lifetime: 30000 # 不能小于30秒，否则默认回到1800秒\n      connection-test-query: SELECT 1\n  redis:\n    host: redis\n    port: 6379\n    database: 0 #操作的是0号数据库\n    timeout: 60s\n    lettuce:\n      shutdown-timeout: 100\n      # pool:\n      #   max-idle: 30\n      #   max-active: 8\n      #   max-wait: 10000\n      #   min-idle: 10', '2aa4f234f9ab62fe99a16d16f19afa46', '2024-08-17 12:51:12', '2025-04-07 15:11:18', 'nacos', '27.154.213.153', '', 'prod', '', '', '', 'yaml', '', '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (129, 'mybatisplus.yaml', 'DEFAULT_GROUP', 'mybatis-plus:\n  mapper-locations: classpath*:/mapper/**/*.xml\n  configuration:\n    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl\n    # log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n  global-config:\n    db-config:\n      logic-delete-field: deleted\n      logic-delete-value: 1\n      logic-not-delete-value: 0\n      id-type: auto\n    banner: off # 关闭mybatisplus启动图标', '653c42737f744dcdc635edf67233c2c4', '2024-12-11 10:32:08', '2024-12-11 10:32:08', 'nacos', '110.87.119.197', '', 'prod', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (130, 'pagehelper.yaml', 'DEFAULT_GROUP', 'pagehelper:\n  helperDialect: mysql\n  supportMethodsArguments: true\n  params: count=countSql\n  page-size-zero: true', '8f74b7b94d79a39bd0a34032c38881ee', '2024-12-11 10:32:42', '2024-12-11 10:32:42', 'nacos', '110.87.119.197', '', 'prod', NULL, NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`, `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (131, 'rabbitmq.yaml', 'DEFAULT_GROUP', 'spring:\n  rabbitmq:\n    host: rabbitmq\n    port: 5672\n    username: root\n    password: xxx\n    virtual-host: youyu\n    listener:\n      simple:\n        acknowledge-mode: auto\n        retry:\n          #是否支持重试\n          enabled: true\n          #最大重试次数\n          max-attempts: 2\n          #最大间隔时间\n          max-interval: 3600\n          #初始重试间隔时间\n          initial-interval: 1000\n          #乘子  重试间隔*乘子得出下次重试间隔  3s  9s  27s\n          multiplier: 3\n        #重试次数超过上面的设置之后是否丢弃（false不丢弃时需要写相应代码将该消息加入死信队列）\n        default-requeue-rejected: false\n', '7cbdade880ab06fb35681a5564ed0384', '2024-12-11 11:00:55', '2025-04-07 15:11:40', 'nacos', '27.154.213.153', '', 'prod', '', '', '', 'yaml', '', '');
COMMIT;

-- ----------------------------
-- Table structure for config_info_aggr
-- ----------------------------
DROP TABLE IF EXISTS `config_info_aggr`;
CREATE TABLE `config_info_aggr` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                    `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                                    `group_id` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'group_id',
                                    `datum_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'datum_id',
                                    `content` longtext COLLATE utf8_bin NOT NULL COMMENT '内容',
                                    `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                    `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL,
                                    `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '租户字段',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';

-- ----------------------------
-- Records of config_info_aggr
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
DROP TABLE IF EXISTS `config_info_beta`;
CREATE TABLE `config_info_beta` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                    `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                                    `group_id` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'group_id',
                                    `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'app_name',
                                    `content` longtext COLLATE utf8_bin NOT NULL COMMENT 'content',
                                    `beta_ips` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT 'betaIps',
                                    `md5` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'md5',
                                    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `src_user` text COLLATE utf8_bin COMMENT 'source user',
                                    `src_ip` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'source ip',
                                    `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '租户字段',
                                    `encrypted_data_key` text COLLATE utf8_bin NOT NULL COMMENT '秘钥',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

-- ----------------------------
-- Records of config_info_beta
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
DROP TABLE IF EXISTS `config_info_tag`;
CREATE TABLE `config_info_tag` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                   `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                                   `group_id` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'group_id',
                                   `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT 'tenant_id',
                                   `tag_id` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'tag_id',
                                   `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'app_name',
                                   `content` longtext COLLATE utf8_bin NOT NULL COMMENT 'content',
                                   `md5` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'md5',
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `src_user` text COLLATE utf8_bin COMMENT 'source user',
                                   `src_ip` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'source ip',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

-- ----------------------------
-- Records of config_info_tag
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation` (
                                        `id` bigint(20) NOT NULL COMMENT 'id',
                                        `tag_name` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'tag_name',
                                        `tag_type` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT 'tag_type',
                                        `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
                                        `group_id` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'group_id',
                                        `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT 'tenant_id',
                                        `nid` bigint(20) NOT NULL AUTO_INCREMENT,
                                        PRIMARY KEY (`nid`),
                                        UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
                                        KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

-- ----------------------------
-- Records of config_tags_relation
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity` (
                                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `group_id` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
                                  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
                                  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
                                  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
                                  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
                                  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
                                  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
                                  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

-- ----------------------------
-- Records of group_capacity
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info` (
                                   `id` bigint(20) unsigned NOT NULL,
                                   `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                   `data_id` varchar(255) COLLATE utf8_bin NOT NULL,
                                   `group_id` varchar(128) COLLATE utf8_bin NOT NULL,
                                   `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'app_name',
                                   `content` longtext COLLATE utf8_bin NOT NULL,
                                   `md5` varchar(32) COLLATE utf8_bin DEFAULT NULL,
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `src_user` text COLLATE utf8_bin,
                                   `src_ip` varchar(50) COLLATE utf8_bin DEFAULT NULL,
                                   `op_type` char(10) COLLATE utf8_bin DEFAULT NULL,
                                   `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '租户字段',
                                   `encrypted_data_key` text COLLATE utf8_bin NOT NULL COMMENT '秘钥',
                                   PRIMARY KEY (`nid`),
                                   KEY `idx_gmt_create` (`gmt_create`),
                                   KEY `idx_gmt_modified` (`gmt_modified`),
                                   KEY `idx_did` (`data_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
                               `role` varchar(50) NOT NULL,
                               `resource` varchar(255) NOT NULL,
                               `action` varchar(8) NOT NULL,
                               UNIQUE KEY `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of permissions
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
                         `username` varchar(50) NOT NULL,
                         `role` varchar(50) NOT NULL,
                         UNIQUE KEY `idx_user_role` (`username`,`role`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of roles
-- ----------------------------
BEGIN;
INSERT INTO `roles` (`username`, `role`) VALUES ('nacos', 'ROLE_ADMIN');
COMMIT;

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `tenant_id` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
                                   `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
                                   `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
                                   `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
                                   `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
                                   `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
                                   `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';

-- ----------------------------
-- Records of tenant_capacity
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `kp` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'kp',
                               `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT 'tenant_id',
                               `tenant_name` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT 'tenant_name',
                               `tenant_desc` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT 'tenant_desc',
                               `create_source` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'create_source',
                               `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
                               `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
                               KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

-- ----------------------------
-- Records of tenant_info
-- ----------------------------
BEGIN;
INSERT INTO `tenant_info` (`id`, `kp`, `tenant_id`, `tenant_name`, `tenant_desc`, `create_source`, `gmt_create`, `gmt_modified`) VALUES (2, '1', 'prod', 'prod', '生产环境配置', 'nacos', 1704544769314, 1704544769314);
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `username` varchar(50) NOT NULL,
                         `password` varchar(500) NOT NULL,
                         `enabled` tinyint(1) NOT NULL,
                         PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('ahai1234', '$2a$10$IcSCN.J2sVWTRNkTiNQG6.I2pKjN40sR/hDEdL7s8XqmLpe/OFah.', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('haidan', '$2a$10$WscbY8BpbxewBcKGjMVCa.STJjLR0eBnc1iXmNkX.8Yx3ZrjXTCji', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('mhtqtyadexnitdji', '$2a$10$jR/ltJ7FlMTKmJbomR2eS.5QC9hHjvcDjMl2tWbuud6/BswDaZWqC', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('msmgzclgdrjfqetc', '$2a$10$CisHgp8nHz1vWh7xA0Yj3O.q.MLaa7QznLoigOAe23CYwQMT/gF7y', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('nacos', '$2a$10$7O5ZRjUYKMtjaEBesozqm..VjHJTY3x2VzfOh7rsE9umkglui6iD.', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('nacos6789', '$2a$10$qrrnSEt4KMuv.IHw4oJzFeeG.uKdqjah9sxfZ1G/z1aed8mhkxLaa', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('nacos8516', '$2a$10$fx5.S7ssRnUTkIKYrThKjeexsJL7hHRFwf7H.AUxmdAJVqrEHy3p2', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('nacos8617', '$2a$10$VfkRFDnDwOUjrY9FaahUW.tjTzq0bFUcvowDU7g9d6qjbhH4PzSBW', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('NacosAD', '$2a$10$YCnQ08tXWJ4yDnvzAAB8Du4Z7.oBaUMb63Ly7aFjzTI/bbizkKOTC', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('nacos_new_user', '$2a$10$j3cKL6z.IW2A2xm9EMMUd.g4yrGFSLtmb2siYRAqduZREpeM9mT8O', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('NAGB6R', '$2a$10$S9cmUTW4HNmx8BLfm1jutuAwyVQ66mF6ifFcAFIgzMwBc.2Ufavey', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('qqqwww1231', '$2a$10$833imb5TBineWYnB2gdvyuxeUGaTvP5C0nPyKtev4E.d.AwCwoJyq', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('qqqwww1240', '$2a$10$RE/aBazjY40zkZoLeP/ZbuQa10WGluce.rbe76RRWXp1Rb0kQ70.m', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('test123', '$2a$10$C0Fg9WGf5YXES9XCtBZQJ.Snm4qal2ONj1hsD0g2TMQeJIma0t9bK', 1);
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES ('wwwdddd1223555', '$2a$10$JxqnTyreJAtvlauVmjnu9uf1sV5WyzA/LZ0CrOXu4lz7KmrP8lyvO', 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
