-- 关闭外键检查
select @@FOREIGN_KEY_CHECKS;
SET @@FOREIGN_KEY_CHECKS = 0;

-- 创建用户表
drop table if exists `user`;
create table `user` (
    `id` bigint primary key auto_increment comment 'ID',
    `openid` varchar(32) unique not null default '' comment 'OpenID',
    `unionid` varchar(32) not null default '' comment 'UnionID',
    `nickname` varchar(32) not null default '' comment '昵称',
    `photo` varchar(500) not null default '' comment '用户头像',
    `email` varchar(64) not null default '' comment '邮箱',
    `phone` char(11) not null default '' comment '手机号',
    -- common columna
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    unique index `uni_openid`(`openid` asc) using btree,
    index `idx_unionid`(`unionid` asc) using btree
) comment '用户表';

-- 创建 OKR 内核表
drop table if exists `okr_core`;
create table `okr_core` (
    `id` bigint primary key auto_increment comment 'ID',
    `celebrate_day` tinyint null default null comment '庆祝日（星期几）',
    `second_quadrant_cycle` int null default null comment '第二象限周期',
    `third_quadrant_cycle` int null default null comment '第三象限周期',
    `is_over` bit not null default b'0' comment '是否结束',
    `summary` text default null comment '总结',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 索引
    unique index `uni_id`(`id` asc) using btree
) comment 'OKR 内核表';

-- 创建个人 OKR 表
drop table if exists `personal_okr`;
create table `personal_okr` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `user_id` bigint not null comment '用户 ID',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`core_id`) references `okr_core`(`id`),
    foreign key (`user_id`) references `user`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    unique index `uni_core_id`(`core_id` asc) using btree,
    index `idx_user_id`(`user_id` asc) using btree
) comment '个人 OKR 表';

-- 创建团队 OKR 表
drop table if exists `team_okr`;
create table `team_okr` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `parent_team_id` bigint comment '从属 OKR ID',
    `manager_id` bigint not null comment '管理这个 OKR 的用户 ID',
    `team_name` varchar(32) not null comment '团队名',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`core_id`) references `okr_core`(`id`),
    foreign key (`parent_team_id`) references `team_okr`(`id`) on delete set null,
    foreign key (`manager_id`) references `user`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    unique index `uni_core_id`(`core_id` asc) using btree ,
    index `idx_manager_id`(`manager_id` asc) using btree
) comment '团队 OKR 表';

delimiter //

CREATE TRIGGER before_insert_team_okr
    BEFORE INSERT ON team_okr
    FOR EACH ROW
    BEGIN
    DECLARE next_id BIGINT;
    SET next_id = (SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='team_okr');
    SET NEW.team_name = CONCAT('团队 #', next_id);
END//

delimiter ;

-- 创建团队个人 OKR 表
drop table if exists `team_personal_okr`;
create table `team_personal_okr` (
     `id` bigint primary key auto_increment comment 'ID',
     `core_id` bigint unique not null comment 'OKR 内核 ID',
     `team_id` bigint not null comment '团队 OKR ID',
     `user_id` bigint not null comment '用户 ID',
    -- common column
     `version` int not null default 0 comment '乐观锁',
     `is_deleted` bit not null default b'0' comment '伪删除标记',
     `create_time` datetime not null default current_timestamp comment '创建时间',
     `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
     foreign key (`core_id`) references `okr_core`(`id`),
     foreign key (`team_id`) references `team_okr`(`id`),
     foreign key (`user_id`) references `user`(`id`),
    -- 索引
     unique index `uni_id`(`id` asc) using btree,
     unique index `uni_core_id`(`core_id` asc) using btree,
     index `idx_team_id`(`team_id` asc) using btree,
     index `idx_user_id`(`user_id` asc) using btree
) comment '创建团队个人 OKR 表';

-- 创建第一象限表
drop table if exists `first_quadrant`;
create table `first_quadrant` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `objective` varchar(128) not null default '' comment '目标',
    `deadline` datetime null default null comment '截止时间',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`core_id`) references `okr_core`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    unique index `uni_core_id`(`core_id` asc) using btree
) comment '第一象限表';

-- 创建关键结果表
drop table if exists `key_result`;
create table `key_result` (
    `id` bigint primary key auto_increment comment 'ID',
    `first_quadrant_id` bigint not null comment '第一象限 ID',
    `content` varchar(128) not null default '' comment '关键结果内容',
    `probability` tinyint not null default 0 comment '完成概率（百分比）',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`first_quadrant_id`) references `first_quadrant`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    index `idx_first_quadrant_id`(`first_quadrant_id` asc) using btree
) comment '关键结果表';

-- 创建第二象限表
drop table if exists `second_quadrant`;
create table `second_quadrant` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `deadline` datetime null default null comment '截止时间',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`core_id`) references `okr_core`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    unique index `uni_core_id`(`core_id` asc) using btree
) comment '第二象限表';

-- 创建 Priority1 表
drop table if exists `priority_number_one`;
create table `priority_number_one` (
    `id` bigint primary key auto_increment comment 'ID',
    `second_quadrant_id` bigint not null comment '第二象限 ID',
    `content` varchar(128) not null default '' comment '计划内容',
    `is_completed` bit not null default b'0' comment '是否完成',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`second_quadrant_id`) references `second_quadrant`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    index `idx_second_quadrant_id`(`second_quadrant_id` asc) using btree
) comment 'Priority1 表';

-- 创建 Priority2 表
drop table if exists `priority_number_two`;
create table `priority_number_two` (
    `id` bigint primary key auto_increment comment 'ID',
    `second_quadrant_id` bigint not null comment '第二象限 ID',
    `content` varchar(128) not null default '' comment '计划内容',
    `is_completed` bit not null default b'0' comment '是否完成',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`second_quadrant_id`) references `second_quadrant`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    index `idx_second_quadrant_id`(`second_quadrant_id` asc) using btree
) comment 'Priority2 表';

-- 创建第三象限表
drop table if exists `third_quadrant`;
create table `third_quadrant` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    `deadline` datetime null default null comment '截止时间',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`core_id`) references `okr_core`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    unique index `uni_core_id`(`core_id` asc) using btree
) comment '第三象限表';

-- 创建行动表
drop table if exists `action`;
create table `action` (
    `id` bigint primary key auto_increment comment 'ID',
    `third_quadrant_id` bigint not null comment '第三象限 ID',
    `content` varchar(128) not null default '' comment '行动内容',
    `is_completed` bit not null default b'0' comment '是否完成',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`third_quadrant_id`) references `third_quadrant`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    index `idx_third_quadrant_id`(`third_quadrant_id` asc) using btree
) comment '行动表';

-- 创建第四象限表
drop table if exists `fourth_quadrant`;
create table `fourth_quadrant` (
    `id` bigint primary key auto_increment comment 'ID',
    `core_id` bigint unique not null comment 'OKR 内核 ID',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`core_id`) references `okr_core`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    unique index `uni_core_id`(`core_id` asc) using btree
) comment '第四象限表';

-- 创建指标表
drop table if exists `status_flag`;
create table `status_flag` (
    `id` bigint primary key auto_increment comment 'ID',
    `fourth_quadrant_id` bigint not null comment '第四象限 ID',
    `label` varchar(128) not null default '' comment '指标内容',
    `color` char(7) not null default '#00ff00' comment '指标颜色',
    -- common column
    `version` int not null default 0 comment '乐观锁',
    `is_deleted` bit not null default b'0' comment '伪删除标记',
    `create_time` datetime not null default current_timestamp comment '创建时间',
    `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    -- 外键约束
    foreign key (`fourth_quadrant_id`) references `fourth_quadrant`(`id`),
    -- 索引
    unique index `uni_id`(`id` asc) using btree,
    index `idx_fourth_quadrant_id`(`fourth_quadrant_id` asc) using btree
) comment '指标表';

-- 开启外键检查
SET @@FOREIGN_KEY_CHECKS = 1;
