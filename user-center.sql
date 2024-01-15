create table user
(
    id           int unsigned zerofill auto_increment
        primary key,
    username     varchar(255)                        null comment '用户昵称',
    userAccount  varchar(255)                        null comment '用户账号',
    avatarUrl    varchar(1024)                       null comment '用户头像',
    gender       tinyint                             null comment '性别',
    userPassword varchar(512)                        null comment '密码',
    phone        varchar(255)                        null comment '电话',
    email        varchar(255)                        null comment '邮箱',
    createTime   timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   timestamp default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete     tinyint   default 0                 not null comment '是否删除',
    userStatus   int       default 0                 not null comment '0 正常 1不正常',
    userRole     int       default 0                 not null comment '0 普通用户 1管理员用户',
    planetCode   varchar(512)                        null comment '星球编号'
);
