-- 创建库
create database if not exists qi_api;

-- 切换库
use qi_api;

-- 用户表
create table if not exists qi_api.user
(
    id             bigint auto_increment comment 'id' primary key,
    userName       varchar(256)                           null comment '用户昵称',
    userAccount    varchar(256)                           not null comment '账号',
    userAvatar     varchar(1024)                          null comment '用户头像',
    email          varchar(256)                           null comment '邮箱',
    gender         varchar(10)                            null comment '性别 0-男 1-女',
    userRole       varchar(256) default 'user'            not null comment '用户角色：user / admin',
    userPassword   varchar(512)                           null comment '密码',
    accessKey      varchar(256)                           null comment 'accessKey',
    secretKey      varchar(256)                           null comment 'secretKey',
    balance        bigint       default 30                not null comment '钱包余额,注册送30币',
    invitationCode varchar(256)                           null comment '邀请码',
    status         tinyint      default 0                 not null comment '账号状态（0- 正常 1- 封号）',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount
        unique (userAccount)
)
    comment '用户';

-- 每日签到表
create table if not exists qi_api.daily_check_in
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '签到人',
    description varchar(256)                       null comment '描述',
    addPoints   bigint   default 10                not null comment '签到增加积分个数',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '每日签到表';

-- 接口信息
create table if not exists qi_api.interface_info
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(256)                           not null comment '接口名称',
    url            varchar(256)                           not null comment '接口地址',
    userId         bigint                                 null comment '发布人',
    method         varchar(256)                           not null comment '请求方法',
    requestParams  text                                   null comment '接口请求参数',
    responseParams text                                   null comment '接口响应参数',
    reduceScore    bigint       default 0                 null comment '扣除积分数',
    requestExample text                                   null comment '请求示例',
    requestHeader  text                                   null comment '请求头',
    responseHeader text                                   null comment '响应头',
    returnFormat   varchar(512) default 'JSON'            null comment '返回格式(JSON等等)',
    description    varchar(256)                           null comment '描述信息',
    status         tinyint      default 0                 not null comment '接口状态（0- 默认下线 1- 上线）',
    totalInvokes   bigint       default 0                 not null comment '接口总调用次数',
    avatarUrl      varchar(1024)                          null comment '接口头像',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除'
)
    comment '接口信息';


-- 产品信息
create table if not exists qi_api.product_info
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(256)                           not null comment '产品名称',
    description    varchar(256)                           null comment '产品描述',
    userId         bigint                                 null comment '创建人',
    total          bigint                                 null comment '金额(分)',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    productType    varchar(256) default 'RECHARGE'        not null comment '产品类型（VIP-会员 RECHARGE-充值,RECHARGEACTIVITY-充值活动）',
    status         tinyint      default 0                 not null comment '商品状态（0- 默认下线 1- 上线）',
    expirationTime datetime                               null comment '过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除'
)
    comment '产品信息';

# 产品订单
create table if not exists qi_api.product_order
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                           not null comment '订单号',
    codeUrl        varchar(256)                           null comment '二维码地址',
    userId         bigint                                 not null comment '创建人',
    productId      bigint                                 not null comment '商品id',
    orderName      varchar(256)                           not null comment '商品名称',
    total          bigint                                 not null comment '金额(分)',
    status         varchar(256) default 'NOTPAY'          not null comment '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
    payType        varchar(256) default 'WX'              not null comment '支付方式（默认 WX- 微信 ZFB- 支付宝）',
    productInfo    text                                   null comment '商品信息',
    formData       text                                   null comment '支付宝formData',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    expirationTime datetime                               null comment '过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '商品订单';

#付款信息
create table if not exists qi_api.payment_info
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                           null comment '商户订单号',
    transactionId  varchar(256)                           null comment '微信支付订单号',
    tradeType      varchar(256)                           null comment '交易类型',
    tradeState     varchar(256)                           null comment '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
    tradeStateDesc varchar(256)                           null comment '交易状态描述',
    successTime    varchar(256)                           null comment '支付完成时间',
    openid         varchar(256)                           null comment '用户标识',
    payerTotal     bigint                                 null comment '用户支付金额',
    currency       varchar(256) default 'CNY'             null comment '货币类型',
    payerCurrency  varchar(256) default 'CNY'             null comment '用户支付币种',
    content        text                                   null comment '接口返回内容',
    total          bigint                                 null comment '总金额(分)',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '付款信息';

-- 用户接口调用表
create table if not exists qi_api.user_interface_invoke
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null comment '调用人id',
    interfaceId  bigint                             not null comment '接口id',
    totalInvokes bigint   default 0                 not null comment '总调用次数',
    status       tinyint  default 0                 not null comment '调用状态（0- 正常 1- 封号）',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '用户接口调用表';

-- 充值活动表
create table if not exists qi_api.recharge_activity
(
    id         bigint auto_increment comment 'id' primary key,
    userId     bigint                             not null comment '用户id',
    productId  bigint                             not null comment '商品id',
    orderNo        varchar(256)                           null comment '商户订单号',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '充值活动表';


INSERT INTO qi_api.interface_info (id, name, url, userId, method, requestParams, reduceScore, requestExample, requestHeader, responseHeader, description, status, totalInvokes, avatarUrl, returnFormat, responseParams, createTime, updateTime, isDelete) VALUES (1703436986248761345, '随机毒鸡汤', 'http://localhost:8090/api/poisonousChickenSoup', 1699981437456797697, 'GET', '[]', 1, ' http://localhost:8090/api/name/poisonousChickenSoup', null, null, null, 1, 67, null, 'JSON', '[{"id":"1695051685885","fieldName":"code","type":"int","desc":"响应码"},{"id":"1695052930602","fieldName":"data.text","type":"string","desc":"随机毒鸡汤"},{"id":"1695052955781","fieldName":"message","type":"string","desc":"响应描述"}]', '2023-09-17 23:53:20', '2023-09-20 19:58:49', 0);
INSERT INTO qi_api.interface_info (id, name, url, userId, method, requestParams, reduceScore, requestExample, requestHeader, responseHeader, description, status, totalInvokes, avatarUrl, returnFormat, responseParams, createTime, updateTime, isDelete) VALUES (1703713253414019074, '获取输入的名称', 'http://localhost:8090/api/name', 1699981437456797697, 'GET', '[{"id":"1695031845159","fieldName":"name","type":"string","desc":"输入的名称","required":"是"}]', 1, 'http://localhost:8090/api/name?name=张三', null, null, '获取输入的名称', 1, 14, 'https://img.qimuu.icu/interface_avatar/1699981437456797697/XqT3Nsto-psc.jfif', 'JSON', '[{"id":"1695105888173","fieldName":"data.name","type":"string","desc":"输入的参数"}]', '2023-09-18 18:11:07', '2023-09-19 15:50:44', 0);
INSERT INTO qi_api.interface_info (id, name, url, userId, method, requestParams, reduceScore, requestExample, requestHeader, responseHeader, description, status, totalInvokes, avatarUrl, returnFormat, responseParams, createTime, updateTime, isDelete) VALUES (1703713999295488002, '随机壁纸', 'http://localhost:8090/api/randomWallpaper', 1699981437456797697, 'GET', '[{"id":"1695032007961","fieldName":"method","type":"string","desc":"\\t输出壁纸端[mobile|pc|zsy]默认为pc","required":"否"},{"id":"1695032018924","fieldName":"lx","type":"string","desc":"\\t选择输出分类[meizi|dongman|fengjing|suiji]，为空随机输出","required":"否"}]', 1, 'http://localhost:8090/api/randomWallpaper?lx=dongman', null, null, null, 1, 42, 'https://img.qimuu.icu/typory/logo.jpg', 'JSON', '[{"id":"1695051751595","fieldName":"code","type":"string","desc":"响应码"},{"id":"1695051832571","fieldName":"data.imgurl","type":"string","desc":"返回的壁纸地址"},{"id":"1695051861456","fieldName":"message","type":"string","desc":"响应消息"}]', '2023-09-18 18:14:05', '2023-09-20 20:16:51', 0);


INSERT INTO qi_api.product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695338876708544514, '100坤币', '增加100坤币到钱包', 1691069533871013889, 1, 100, 'RECHARGEACTIVITY', 1, null, '2023-08-26 15:34:20', '2023-08-28 12:58:30', 0);
INSERT INTO qi_api.product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695773972037839073, '9999坤币', '增加9999坤币到钱包', 1691069533871013889, 699, 9999, 'RECHARGE', 1, '2023-08-28 13:01:34', '2023-08-27 20:35:34', '2023-08-27 20:41:29', 0);
INSERT INTO qi_api.product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695776766919888897, '1000坤币', '增加1000坤币到钱包', 1691069533871013889, 99, 1000, 'RECHARGE', 1, null, '2023-08-27 20:34:21', '2023-08-27 20:34:21', 0);
INSERT INTO qi_api.product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695777072030339073, '3000坤币', '增加3000坤币到钱包', 1691069533871013889, 199, 3000, 'RECHARGE', 1, null, '2023-08-27 20:35:34', '2023-08-27 20:41:29', 0);
INSERT INTO qi_api.product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695777203236556802, '15999坤币', '增加15999坤币到钱包', 1691069533871013889, 888, 15999, 'RECHARGE', 1, null, '2023-08-27 20:36:05', '2023-08-28 13:02:25', 0);
INSERT INTO qi_api.product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695778320091631617, '18999坤币', '增加18999坤币到钱包', 1691069533871013889, 999, 18999, 'RECHARGE', 1, null, '2023-08-27 20:40:32', '2023-08-28 13:02:42', 0);
INSERT INTO qi_api.product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1697087470134259713, '10坤币', '签到获取', 1692848556158709762, 0, 10, 'RECHARGE', 0, null, '2023-08-31 11:22:37', '2023-08-31 11:22:37', 1);
