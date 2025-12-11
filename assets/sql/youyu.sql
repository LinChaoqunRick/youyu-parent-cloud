/*
 Navicat Premium Data Transfer

 Source Server         : *.*.*.*
 Source Server Type    : MySQL
 Source Server Version : 50730 (5.7.30)
 Source Host           : *.*.*.*
 Source Schema         : youyu

 Target Server Type    : MySQL
 Target Server Version : 50730 (5.7.30)
 File Encoding         : 65001

 Date: 07/04/2025 14:42:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bs_album
-- ----------------------------
DROP TABLE IF EXISTS `bs_album`;
CREATE TABLE `bs_album` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '相册名称',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户',
  `authorized_users` varchar(1000) DEFAULT '' COMMENT '授权用户',
  `cover_image_id` bigint(20) DEFAULT NULL COMMENT '封面图片id',
  `cover` varchar(600) DEFAULT NULL COMMENT '封面',
  `content` varchar(255) NOT NULL COMMENT '相册描述',
  `subscribe_count` bigint(20) DEFAULT '0' COMMENT '订阅数量',
  `like_count` bigint(20) DEFAULT '0' COMMENT '点赞数量',
  `open` int(11) NOT NULL DEFAULT '1' COMMENT '是否开放',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `bs_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_album_image
-- ----------------------------
DROP TABLE IF EXISTS `bs_album_image`;
CREATE TABLE `bs_album_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `album_id` bigint(20) NOT NULL COMMENT '所属相册',
  `name` varchar(255) NOT NULL COMMENT '图片名称',
  `path` varchar(255) NOT NULL COMMENT '图片路径',
  `content` varchar(255) DEFAULT NULL COMMENT '图片描述',
  `size` bigint(20) NOT NULL COMMENT '图片大小',
  `open` int(11) NOT NULL DEFAULT '1' COMMENT '是否开放',
  `like_count` bigint(20) DEFAULT '0' COMMENT '点赞数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `album_id` (`album_id`),
  CONSTRAINT `album_id` FOREIGN KEY (`album_id`) REFERENCES `bs_album` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=429 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_category
-- ----------------------------
DROP TABLE IF EXISTS `bs_category`;
CREATE TABLE `bs_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '分类名',
  `pid` bigint(20) DEFAULT '-1' COMMENT '父Id',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `status` varchar(255) DEFAULT '0' COMMENT '状态：0正常，1:禁用',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_client
-- ----------------------------
DROP TABLE IF EXISTS `bs_client`;
CREATE TABLE `bs_client` (
  `client_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_name` varchar(255) NOT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `client_logo_url` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` int(11) DEFAULT '0',
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_column
-- ----------------------------
DROP TABLE IF EXISTS `bs_column`;
CREATE TABLE `bs_column` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '归属的用户id',
  `title` varchar(255) DEFAULT NULL COMMENT '专栏名称',
  `content` varchar(500) DEFAULT NULL COMMENT '专栏描述',
  `cover` varchar(255) DEFAULT '封面',
  `status` varchar(255) DEFAULT '0' COMMENT '状态 0：正常 1：禁用',
  `is_top` char(1) DEFAULT '0',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000006 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_column_subscribe
-- ----------------------------
DROP TABLE IF EXISTS `bs_column_subscribe`;
CREATE TABLE `bs_column_subscribe` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '订阅人',
  `column_id` bigint(20) DEFAULT NULL COMMENT '专栏id',
  `create_time` datetime DEFAULT NULL COMMENT '订阅时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_comment
-- ----------------------------
DROP TABLE IF EXISTS `bs_comment`;
CREATE TABLE `bs_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `post_id` bigint(20) DEFAULT NULL COMMENT '文章id',
  `root_id` bigint(20) DEFAULT '-1' COMMENT '根评论id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `user_id_to` bigint(20) DEFAULT '-1' COMMENT '被回复的用户id',
  `reply_id` bigint(20) DEFAULT '-1' COMMENT '回复了哪条子评论',
  `content` longtext COMMENT '评论内容',
  `adcode` int(11) DEFAULT NULL COMMENT '省份编码',
  `support_count` bigint(20) DEFAULT '0' COMMENT '支持数量',
  `oppose_count` bigint(20) DEFAULT '0' COMMENT '反对数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=481 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `bs_comment_like`;
CREATE TABLE `bs_comment_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `comment_id` bigint(20) DEFAULT NULL COMMENT '评论Id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '点赞人',
  `user_id_to` bigint(20) DEFAULT NULL COMMENT '被点赞人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_favorites
-- ----------------------------
DROP TABLE IF EXISTS `bs_favorites`;
CREATE TABLE `bs_favorites` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '归属用户id',
  `name` varchar(20) NOT NULL COMMENT '收藏夹名称',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面地址',
  `open` int(11) DEFAULT '1' COMMENT '是否公开，1：是，0否',
  `is_top` int(255) DEFAULT '0' COMMENT '是否置顶',
  `like_count` bigint(255) DEFAULT '0' COMMENT '点赞数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_message
-- ----------------------------
DROP TABLE IF EXISTS `bs_message`;
CREATE TABLE `bs_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '留言主键',
  `root_id` bigint(20) DEFAULT '-1',
  `user_id` bigint(20) DEFAULT NULL,
  `user_id_to` bigint(20) DEFAULT NULL,
  `nickname` varchar(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `content` varchar(255) DEFAULT NULL,
  `adcode` int(11) DEFAULT NULL COMMENT '省份编号',
  `support_count` bigint(20) DEFAULT '0',
  `oppose_count` bigint(20) DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_moment
-- ----------------------------
DROP TABLE IF EXISTS `bs_moment`;
CREATE TABLE `bs_moment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '发表用户',
  `content` varchar(1000) NOT NULL COMMENT '内容',
  `mood` int(11) DEFAULT NULL COMMENT '此刻心情',
  `topic_id` int(11) DEFAULT NULL COMMENT '话题编号',
  `images` mediumtext COMMENT '图片',
  `longitude` varchar(255) DEFAULT NULL COMMENT '经度',
  `latitude` varchar(255) DEFAULT NULL COMMENT '维度',
  `location` varchar(600) DEFAULT NULL COMMENT '地址',
  `adcode` int(11) DEFAULT NULL COMMENT '区域编号',
  `support_count` int(11) DEFAULT '0' COMMENT '点赞数',
  `oppose_count` int(11) DEFAULT '0' COMMENT '反对数',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=344 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_moment_comment
-- ----------------------------
DROP TABLE IF EXISTS `bs_moment_comment`;
CREATE TABLE `bs_moment_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `moment_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '回复的时刻id',
  `root_id` bigint(20) DEFAULT '-1' COMMENT '根评论id',
  `user_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '回复人id',
  `user_id_to` bigint(20) DEFAULT '-1' COMMENT '被回复人id',
  `reply_id` bigint(20) DEFAULT '-1' COMMENT '回复了哪条子评论',
  `images` mediumtext COMMENT '图片',
  `content` varchar(1000) NOT NULL COMMENT '回复内容',
  `adcode` int(11) DEFAULT NULL COMMENT '省份编码',
  `support_count` int(11) DEFAULT '0' COMMENT '支持数量',
  `oppose_count` int(11) DEFAULT '0' COMMENT '反对数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=210 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_moment_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `bs_moment_comment_like`;
CREATE TABLE `bs_moment_comment_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `comment_id` bigint(20) DEFAULT NULL COMMENT '时刻Id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '点赞人',
  `user_id_to` bigint(20) DEFAULT NULL COMMENT '被点赞人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for bs_moment_like
-- ----------------------------
DROP TABLE IF EXISTS `bs_moment_like`;
CREATE TABLE `bs_moment_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `moment_id` bigint(20) DEFAULT NULL COMMENT '时刻Id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '点赞人',
  `user_id_to` bigint(20) DEFAULT NULL COMMENT '被点赞人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for bs_note
-- ----------------------------
DROP TABLE IF EXISTS `bs_note`;
CREATE TABLE `bs_note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '笔记标题',
  `user_id` bigint(20) DEFAULT NULL COMMENT '所属用户',
  `introduce` varchar(255) DEFAULT NULL COMMENT '说明',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面',
  `type` char(1) DEFAULT NULL COMMENT '类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_note_chapter
-- ----------------------------
DROP TABLE IF EXISTS `bs_note_chapter`;
CREATE TABLE `bs_note_chapter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `note_id` bigint(20) DEFAULT NULL COMMENT '所属笔记',
  `parent_id` bigint(20) DEFAULT '-1' COMMENT '父目录',
  `user_ids` varchar(255) DEFAULT NULL COMMENT '创作人',
  `title` varchar(255) DEFAULT NULL COMMENT '章节标题',
  `content` longtext COMMENT '章节内容',
  `order` int(11) DEFAULT '0' COMMENT '排序号',
  `view_count` bigint(20) DEFAULT '0' COMMENT '浏览量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_post
-- ----------------------------
DROP TABLE IF EXISTS `bs_post`;
CREATE TABLE `bs_post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `summary` varchar(1024) DEFAULT NULL COMMENT '文章摘要',
  `content` longtext COMMENT '文章内容',
  `type` int(11) DEFAULT '1' COMMENT '文章类型：1文章 2草稿 预留字段暂不使用',
  `create_type` int(11) DEFAULT '0' COMMENT '文章创作类型：0：原创 1：转载 2：翻译',
  `category_id` bigint(20) DEFAULT NULL COMMENT '所属分类Id',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签',
  `thumbnail` varchar(1024) DEFAULT NULL COMMENT '缩略图地址',
  `column_ids` varchar(100) DEFAULT NULL COMMENT '专栏编号',
  `adcode` varchar(255) DEFAULT NULL COMMENT '省份编码',
  `is_top` int(11) DEFAULT '0' COMMENT '是否置顶（0 否 1 是）',
  `status` int(11) DEFAULT '0' COMMENT '状态（0公开 1隐藏）',
  `support_count` int(11) DEFAULT '0' COMMENT '支持数量',
  `oppose_count` int(11) DEFAULT '0' COMMENT '反对数量',
  `view_count` int(11) DEFAULT '0' COMMENT '访问量',
  `is_comment` int(11) DEFAULT '1' COMMENT '是否允许评论（0否 1是）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `original_link` varchar(1024) DEFAULT NULL COMMENT '原文链接',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标志 0：未删除 1：删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=316 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_post_collect
-- ----------------------------
DROP TABLE IF EXISTS `bs_post_collect`;
CREATE TABLE `bs_post_collect` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` bigint(20) NOT NULL COMMENT '文章编号',
  `favorites_id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL COMMENT '收藏人id',
  `user_id_to` bigint(20) DEFAULT NULL COMMENT '被收藏人id',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `favorites_id` (`favorites_id`),
  KEY `postid` (`post_id`),
  KEY `userid` (`user_id`),
  CONSTRAINT `favorites_id` FOREIGN KEY (`favorites_id`) REFERENCES `bs_favorites` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `postid` FOREIGN KEY (`post_id`) REFERENCES `bs_post` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `userid` FOREIGN KEY (`user_id`) REFERENCES `bs_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_post_like
-- ----------------------------
DROP TABLE IF EXISTS `bs_post_like`;
CREATE TABLE `bs_post_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞表主键',
  `post_id` bigint(20) DEFAULT NULL COMMENT '帖子id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '点赞人',
  `user_id_to` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_profile_menu
-- ----------------------------
DROP TABLE IF EXISTS `bs_profile_menu`;
CREATE TABLE `bs_profile_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `show_home` int(11) DEFAULT '1' COMMENT '显示动态',
  `show_moment` int(11) DEFAULT NULL COMMENT '显示时刻',
  `show_post` int(11) DEFAULT '1' COMMENT '显示帖子',
  `show_note` int(11) DEFAULT '1' COMMENT '显示笔记',
  `show_column` int(11) DEFAULT '1' COMMENT '显示专栏',
  `show_favorites` int(11) DEFAULT '1' COMMENT '显示收藏',
  `show_follow` int(11) DEFAULT '1' COMMENT '显示关注',
  `show_fans` int(11) DEFAULT '1' COMMENT '显示粉丝',
  `show_album` int(11) DEFAULT '1' COMMENT '显示相册',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_route
-- ----------------------------
DROP TABLE IF EXISTS `bs_route`;
CREATE TABLE `bs_route` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '路由名称',
  `code` varchar(255) DEFAULT NULL COMMENT '路由编码',
  `pid` int(11) NOT NULL DEFAULT '-1' COMMENT '父id',
  `description` varchar(255) DEFAULT NULL COMMENT '路由描述',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_user
-- ----------------------------
DROP TABLE IF EXISTS `bs_user`;
CREATE TABLE `bs_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(11) DEFAULT NULL COMMENT '用户名、手机号码',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `nickname` varchar(20) NOT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT 'https://youyu-source.oss-cn-beijing.aliyuncs.com/avatar/default/boy.png' COMMENT '头像',
  `sex` int(11) DEFAULT '0' COMMENT '性别',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱地址',
  `adcode` int(11) DEFAULT NULL COMMENT '省份编号',
  `level` int(11) DEFAULT NULL COMMENT '等级',
  `homepage` varchar(255) DEFAULT NULL COMMENT '个人主页',
  `github_id` varchar(100) DEFAULT NULL COMMENT 'github用户id',
  `qq_id` varchar(100) DEFAULT NULL COMMENT 'qq用户id',
  `register_date` date DEFAULT NULL COMMENT '注册日期',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `signature` varchar(255) DEFAULT NULL COMMENT '个性签名',
  `status` varchar(255) NOT NULL DEFAULT '0' COMMENT '启用状态',
  `deleted` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10046 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for bs_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `bs_user_follow`;
CREATE TABLE `bs_user_follow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '关注人id',
  `user_id_to` bigint(20) DEFAULT NULL COMMENT '被关注人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `menu_name` varchar(64) NOT NULL DEFAULT 'NULL' COMMENT '菜单名',
  `path` varchar(200) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` int(11) DEFAULT '0' COMMENT '是否删除（0未删除 1已删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `role_key` varchar(100) DEFAULT NULL COMMENT '角色权限字符串',
  `status` char(1) DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
  `del_flag` int(11) DEFAULT '0' COMMENT 'del_flag',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '菜单id',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sys_role_route
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_route`;
CREATE TABLE `sys_role_route` (
  `role_id` int(11) DEFAULT NULL,
  `route_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '角色id',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10046 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
