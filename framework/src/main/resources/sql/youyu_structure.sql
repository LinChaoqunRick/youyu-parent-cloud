/*
Navicat MySQL Data Transfer

Source Server         : youyu-server
Source Server Version : 80032
Source Host           : 124.222.79.236:3306
Source Database       : youyu

Target Server Type    : MYSQL
Target Server Version : 80032
File Encoding         : 65001

Date: 2023-05-02 21:31:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for bs_category
-- ----------------------------
DROP TABLE IF EXISTS `bs_category`;
CREATE TABLE `bs_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '分类名',
  `pid` bigint DEFAULT '-1' COMMENT '父Id',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `status` varchar(255) DEFAULT '0' COMMENT '状态：0正常，1:禁用',
  `deleted` int DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_column
-- ----------------------------
DROP TABLE IF EXISTS `bs_column`;
CREATE TABLE `bs_column` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '归属的用户id',
  `title` varchar(255) DEFAULT NULL COMMENT '专栏名称',
  `content` varchar(500) DEFAULT NULL COMMENT '专栏描述',
  `cover` varchar(255) DEFAULT '封面',
  `status` varchar(255) DEFAULT '0' COMMENT '状态 0：正常 1：禁用',
  `is_top` char(1) DEFAULT '0',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_column_subscribe
-- ----------------------------
DROP TABLE IF EXISTS `bs_column_subscribe`;
CREATE TABLE `bs_column_subscribe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint DEFAULT NULL COMMENT '订阅人',
  `column_id` bigint DEFAULT NULL COMMENT '专栏id',
  `create_time` datetime DEFAULT NULL COMMENT '订阅时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_comment
-- ----------------------------
DROP TABLE IF EXISTS `bs_comment`;
CREATE TABLE `bs_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint DEFAULT NULL COMMENT '文章id',
  `root_id` bigint DEFAULT '-1' COMMENT '根评论id',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `user_id_to` bigint DEFAULT '-1' COMMENT '被回复的用户id',
  `reply_id` bigint DEFAULT '-1' COMMENT '回复了哪条子评论',
  `content` longtext COMMENT '评论内容',
  `support_count` bigint DEFAULT '0' COMMENT '支持数量',
  `oppose_count` bigint DEFAULT '0' COMMENT '反对数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=373 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `bs_comment_like`;
CREATE TABLE `bs_comment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `comment_id` bigint DEFAULT NULL COMMENT '评论Id',
  `user_id` bigint DEFAULT NULL COMMENT '点赞人',
  `user_id_to` bigint DEFAULT NULL COMMENT '被点赞人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_note
-- ----------------------------
DROP TABLE IF EXISTS `bs_note`;
CREATE TABLE `bs_note` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '笔记标题',
  `user_id` bigint DEFAULT NULL COMMENT '所属用户',
  `introduce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '说明',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_note_chapter
-- ----------------------------
DROP TABLE IF EXISTS `bs_note_chapter`;
CREATE TABLE `bs_note_chapter` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `note_id` bigint DEFAULT NULL COMMENT '所属笔记',
  `parent_id` bigint DEFAULT '-1' COMMENT '父目录',
  `user_ids` varchar(255) DEFAULT NULL COMMENT '创作人',
  `title` varchar(255) DEFAULT NULL COMMENT '章节标题',
  `content` longtext COMMENT '章节内容',
  `order` int DEFAULT '0' COMMENT '排序号',
  `view_count` bigint DEFAULT '0' COMMENT '浏览量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_post
-- ----------------------------
DROP TABLE IF EXISTS `bs_post`;
CREATE TABLE `bs_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `summary` varchar(1024) DEFAULT NULL COMMENT '文章摘要',
  `content` longtext COMMENT '文章内容',
  `type` char(1) DEFAULT '2' COMMENT '文章类型：1文章 2草稿 预留字段暂不使用',
  `create_type` char(1) DEFAULT '0' COMMENT '文章创作类型：0：原创 1：转载 2：翻译',
  `category_id` bigint DEFAULT NULL COMMENT '所属分类Id',
  `tags` varchar(255) DEFAULT NULL,
  `thumbnail` varchar(1024) DEFAULT NULL COMMENT '缩略图地址',
  `column_id` bigint DEFAULT NULL COMMENT '专栏编号',
  `is_top` char(1) DEFAULT '0' COMMENT '是否置顶（0 否 1 是）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0已发布 1草稿）',
  `view_count` int DEFAULT '0' COMMENT '访问量',
  `is_comment` char(1) CHARACTER SET armscii8 COLLATE armscii8_general_ci DEFAULT '1' COMMENT '是否允许评论（0否 1是）',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `original_link` varchar(1024) DEFAULT NULL COMMENT '原文链接',
  `deleted` int DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=280 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_post_collect
-- ----------------------------
DROP TABLE IF EXISTS `bs_post_collect`;
CREATE TABLE `bs_post_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` bigint DEFAULT NULL COMMENT '文章编号',
  `user_id` bigint DEFAULT NULL COMMENT '收藏人id',
  `user_id_to` bigint DEFAULT NULL COMMENT '被收藏人id',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_post_like
-- ----------------------------
DROP TABLE IF EXISTS `bs_post_like`;
CREATE TABLE `bs_post_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞表主键',
  `post_id` bigint DEFAULT NULL COMMENT '帖子id',
  `user_id` bigint DEFAULT NULL COMMENT '点赞人',
  `user_id_to` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_route
-- ----------------------------
DROP TABLE IF EXISTS `bs_route`;
CREATE TABLE `bs_route` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '路由名称',
  `code` varchar(255) DEFAULT NULL COMMENT '路由编码',
  `pid` int NOT NULL DEFAULT '-1' COMMENT '父id',
  `description` varchar(255) DEFAULT NULL COMMENT '路由描述',
  `deleted` int DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_user
-- ----------------------------
DROP TABLE IF EXISTS `bs_user`;
CREATE TABLE `bs_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户名、手机号码',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'https://youyu-source.oss-cn-beijing.aliyuncs.com/avatar/default/boy.png' COMMENT '头像',
  `sex` int DEFAULT '0' COMMENT '性别',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱地址',
  `level` int DEFAULT NULL COMMENT '等级',
  `homepage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '个人主页',
  `register_date` date DEFAULT NULL COMMENT '注册日期',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `signature` varchar(255) DEFAULT NULL COMMENT '个性签名',
  `status` varchar(255) NOT NULL DEFAULT '0' COMMENT '启用状态',
  `deleted` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10041 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for bs_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `bs_user_follow`;
CREATE TABLE `bs_user_follow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '关注人id',
  `user_id_to` bigint DEFAULT NULL COMMENT '被关注人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_name` varchar(64) NOT NULL DEFAULT 'NULL' COMMENT '菜单名',
  `path` varchar(200) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` int DEFAULT '0' COMMENT '是否删除（0未删除 1已删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单表';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `role_key` varchar(100) DEFAULT NULL COMMENT '角色权限字符串',
  `status` char(1) DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
  `del_flag` int DEFAULT '0' COMMENT 'del_flag',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `menu_id` bigint NOT NULL DEFAULT '0' COMMENT '菜单id',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for sys_role_route
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_route`;
CREATE TABLE `sys_role_route` (
  `role_id` int DEFAULT NULL,
  `route_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `role_id` bigint NOT NULL DEFAULT '0' COMMENT '角色id',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10041 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
