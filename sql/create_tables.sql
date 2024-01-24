-- 关闭外键检查
select @@FOREIGN_KEY_CHECKS;
SET @@FOREIGN_KEY_CHECKS = 0;

-- 创建用户表
drop table if exists `user`;
create table `user` (
    `id` bigint primary key auto_increment comment 'ID',
    `openid` varchar(32) not null default '' comment 'OpenID',
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

insert into `user` (openid, nickname, email, phone, photo)
values
    ('c550610ee39b4495a5f09169246efbc0', '李明洋', 'abc123@example.com', '71938503742', 'https://d-ssl.dtstatic.com/uploads/blog/202008/01/20200801154914_bypvu.thumb.300_300_c.jpg_webp'),
    ('5442d705ed5d4b859170f24dff720172', '王美华', 'testmail@gmail.com', '62847190356', 'https://d-ssl.dtstatic.com/uploads/blog/202008/01/20200801154914_xkvxl.thumb.300_300_c.jpg_webp'),
    ('cd71f3dfabf1439cbba1a0c6fbe65945', '张小明', 'fake_email@outlook.com', '83920567251', 'https://d-ssl.dtstatic.com/uploads/blog/202008/01/20200801154915_eebwi.thumb.300_300_c.jpg_webp'),
    ('ddf88e70bf10416d941238f5dfb49359', '刘大伟', 'randomemail@yahoo.com', '15630928475', 'https://d-ssl.dtstatic.com/uploads/blog/202008/01/20200801154915_dgzju.thumb.300_300_c.jpg_webp'),
    ('814e150a0d1742fab82761f0d618f589', '陈晓丽', 'examplemail@hotmail.com', '40286715923', 'https://d-ssl.dtstatic.com/uploads/blog/202008/01/20200801154916_yphsj.thumb.300_300_c.jpg_webp'),
    ('66d25720ce9f49bf9419e8d08fef9b77', '杨建国', 'mytestmail@qq.com', '57391062847', 'https://d-ssl.dtstatic.com/uploads/blog/202008/01/20200801154916_oljdi.thumb.300_300_c.jpg_webp'),
    ('27a012bd862c4d58a201f18d68d6e776', '黄军营', 'tempmail@inbox.com', '92035671824', 'https://c-ssl.dtstatic.com/uploads/blog/202011/06/20201106170151_6123e.thumb.400_0.jpeg'),
    ('e86d648a447f4523b7c07c281464309f', '吴新宇', 'dummyemail@abc.net', '36584729103', 'https://c-ssl.dtstatic.com/uploads/blog/202011/06/20201106170151_ce59a.thumb.400_0.jpeg'),
    ('71d8cd7becf74b8c87a544fa663a6dca', '周佳音', 'emailtest@fake.org', '80246713592', 'https://d-ssl.dtstatic.com/uploads/item/202105/16/20210516115918_uaMtX.thumb.400_0.jpeg_webp'),
    ('6949de3eef984a0ebd4d5403f337d894', '徐博文', 'newmail@mailbox.co', '14820395672', 'https://d-ssl.dtstatic.com/uploads/item/202007/07/20200707004146_mRzmN.thumb.400_0.jpeg_webp'),
    ('7c4c2f501dd4421f8d7a403796831ff2', '孙嘉乐', 'mailer123@nomail.com', '93167420586', 'https://d-ssl.dtstatic.com/uploads/blog/202008/27/20200827213641_fxwwf.thumb.400_0.jpg_webp'),
    ('b95bbe99e2194a28a1df04becce3c94b', '马云飞', 'notreal@email.org', '25781039465', 'https://d-ssl.dtstatic.com/uploads/blog/202207/04/20220704123758_ad25f.thumb.400_0.jpg_webp'),
    ('e8549a99216e49e5b5e17c97006ba32f', '朱春霞', 'fakemail@nonexistent.net', '60928357142', 'https://c-ssl.dtstatic.com/uploads/blog/202107/29/20210729192509_00acf.thumb.400_0.jpeg'),
    ('36b37bf1bbe642f2a4d4a9e91f742740', '胡雨婷', 'blankmail@example.org', '71492508363', 'https://c-ssl.dtstatic.com/uploads/blog/202102/19/20210219185246_92cf8.thumb.400_0.jpg'),
    ('4ad347d5550148fcb24753d64bcbeb14', '高星辰', 'imaginarymail@test.com', '83612947058', 'https://d-ssl.dtstatic.com/uploads/blog/202207/04/20220704123758_6a654.thumb.400_0.jpg_webp'),
    ('4012c9ccbd4243bc970e7f417c4dd4f7', '王一', 'testemail@random.net', '42918503726', 'https://c-ssl.dtstatic.com/uploads/blog/202009/18/20200918210548_4d005.thumb.400_0.jpg'),
    ('6871a22234d447e58005a8c3de0852a8', '李小', 'pretendmail@mailbox.org', '56273910845', 'https://c-ssl.dtstatic.com/uploads/blog/202009/18/20200918210547_0b220.thumb.400_0.jpeg'),
    ('5bc6cfbb0d964a9c8e2f2bd39d8f05c3', '张美', 'tempmailer@dummy.com', '79103268457', 'https://c-ssl.dtstatic.com/uploads/blog/202201/12/20220112144856_bb515.thumb.400_0.jpg'),
    ('a16bfdc390f44302be0f0c187b871405', '刘阳', 'bogusmail@example.net', '36587421059', 'https://c-ssl.dtstatic.com/uploads/item/201806/26/20180626110230_VjeyZ.thumb.400_0.jpeg'),
    ('544d69d405234b16aa96ba8c136c3f04', '陈明', 'unrealmail@testmail.com', '90285736149', 'https://c-ssl.dtstatic.com/uploads/blog/202009/18/20200918210552_263be.thumb.400_0.jpg')
;


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
