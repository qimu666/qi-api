-- 创建库
create database if not exists qi_api_db;

-- 切换库
use qi_api_db;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userName     varchar(256)                           null comment '用户昵称',
    userAccount  varchar(256)                           not null comment '账号',
    userAvatar   varchar(1024)                          null comment '用户头像',
    accessKey    varchar(256)                           null comment 'accessKey',
    secretKey    varchar(256)                           null comment 'secretKey',
    gender       tinyint                                null comment '性别',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user / admin',
    userPassword varchar(512)                           not null comment '密码',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount
        unique (userAccount)
) comment '用户';


-- 接口信息
create table if not exists interface_info
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(256)                       not null comment '接口名称',
    url            varchar(256)                       not null comment '接口地址',
    userId         bigint                             null comment '发布人',
    method         varchar(256)                       not null comment '请求方法',
    requestParams  text                               null comment '接口请求参数',
    description    varchar(256)                       null comment '描述信息',
    requestExample text                               null comment '请求示例',
    requestHeader  text                               null comment '请求头',
    responseHeader text                               null comment '响应头',
    status         tinyint  default 0                 not null comment '接口状态（0- 默认下线 1- 上线）',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除'
) comment '接口信息';



create table if not exists interface_order
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                          not null comment '订单号',
    codeUrl        varchar(256)                          null comment '二维码地址',
    userId         bigint                                not null comment '创建人',
    interfaceId    bigint                                not null comment '接口id',
    orderName      varchar(256)                          not null comment '商品名称',
    Total          bigint                                not null comment '金额(分)',
    status         varchar(10) default 'NOTPAY'          not null comment '接口订单状态(SUCCESS：支付成功
                                                                                REFUND：转入退款
                                                                                NOTPAY：未支付
                                                                                CLOSED：已关闭
                                                                                REVOKED：已撤销（仅付款码支付会返回）
                                                                                USERPAYING：用户支付中（仅付款码支付会返回）
                                                                                PAYERROR：支付失败（仅付款码支付会返回）)',
    payType        varchar(10) default 'WX'              not null comment '支付方式（默认 WX- 微信 ZFB- 支付宝）',
    expirationTime datetime                              null comment '过期时间',
    createTime     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '接口订单';


create table if not exists payment_info
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                          null comment '商户订单号',
    transactionId  varchar(256)                          null comment '微信支付订单号',
    tradeType      varchar(256)                          null comment '交易类型',
    tradeState     varchar(10)                           null comment '交易状态(SUCCESS：支付成功
                                                                                REFUND：转入退款
                                                                                NOTPAY：未支付
                                                                                CLOSED：已关闭
                                                                                REVOKED：已撤销（仅付款码支付会返回）
                                                                                USERPAYING：用户支付中（仅付款码支付会返回）
                                                                                PAYERROR：支付失败（仅付款码支付会返回）)',
    tradeStateDesc varchar(256)                          null comment '交易状态描述',
    successTime    varchar(256)                          null comment '支付完成时间',
    openid         varchar(256)                          null comment '用户标识',
    payerTotal     bigint                                null comment '用户支付金额',
    currency       varchar(10) default 'CNY'             null comment '货币类型',
    payerCurrency  varchar(10) default 'CNY'             null comment '用户支付币种',
    Content        text                                  null comment '接口返回内容',
    Total          bigint                                null comment '总金额(分)',
    createTime     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '付款信息';



INSERT INTO interface_info (name, url, userId, method, requestParams, description, requestExample, requestHeader,
                            responseHeader, status, createTime, updateTime, isDelete)
VALUES ('接口1', 'http://example.com/api/1', 1, 'GET', 'param1=value1&param2=value2', '接口1的描述信息', '请求示例1', '请求头1', '响应头1',
        1, '2022-01-01 10:00:00', '2022-01-01 10:00:00', 0),
       ('接口2', 'http://example.com/api/2', 2, 'POST', 'param1=value1&param2=value2', '接口2的描述信息', '请求示例2', '请求头2',
        '响应头2', 1, '2022-01-02 10:00:00', '2022-01-02 10:00:00', 0),
       ('接口3', 'http://example.com/api/3', 3, 'GET', 'param1=value1&param2=value2', '接口3的描述信息', '请求示例3', '请求头3', '响应头3',
        1, '2022-01-03 10:00:00', '2022-01-03 10:00:00', 0),
       ('接口4', 'http://example.com/api/4', 4, 'POST', 'param1=value1&param2=value2', '接口4的描述信息', '请求示例4', '请求头4',
        '响应头4', 1, '2022-01-04 10:00:00', '2022-01-04 10:00:00', 0),
       ('接口5', 'http://example.com/api/5', 5, 'GET', 'param1=value1&param2=value2', '接口5的描述信息', '请求示例5', '请求头5', '响应头5',
        1, '2022-01-05 10:00:00', '2022-01-05 10:00:00', 0),
       ('接口6', 'http://example.com/api/6', 6, 'POST', 'param1=value1&param2=value2', '接口6的描述信息', '请求示例6', '请求头6',
        '响应头6', 1, '2022-01-06 10:00:00', '2022-01-06 10:00:00', 0),
       ('接口7', 'http://example.com/api/7', 7, 'GET', 'param1=value1&param2=value2', '接口7的描述信息', '请求示例7', '请求头7', '响应头7',
        1, '2022-01-07 10:00:00', '2022-01-07 10:00:00', 0),
       ('接口8', 'http://example.com/api/8', 8, 'POST', 'param1=value1&param2=value2', '接口8的描述信息', '请求示例8', '请求头8',
        '响应头8', 1, '2022-01-08 10:00:00', '2022-01-08 10:00:00', 0),
       ('接口9', 'http://example.com/api/9', 9, 'GET', 'param1=value1&param2=value2', '接口9的描述信息', '请求示例9', '请求头9', '响应头9',
        1, '2022-01-09 10:00:00', '2022-01-09 10:00:00', 0),
       ('接口10', 'http://example.com/api/10', 10, 'POST', 'param1=value1&param2=value2', '接口10的描述信息', '请求示例10', '请求头10',
        '响应头10', 1, '2022-01-10 10:00:00', '2022-01-10 10:00:00', 0),
       ('接口11', 'http://example.com/api/11', 11, 'GET', 'param1=value1&param2=value2', '接口11的描述信息', '请求示例11', '请求头11',
        '响应头11', 1, '2022-01-11 10:00:00', '2022-01-11 10:00:00', 0),
       ('接口12', 'http://example.com/api/12', 12, 'POST', 'param1=value1&param2=value2', '接口12的描述信息', '请求示例12', '请求头12',
        '响应头12', 1, '2022-01-12 10:00:00', '2022-01-12 10:00:00', 0),
       ('接口13', 'http://example.com/api/13', 13, 'GET', 'param1=value1&param2=value2', '接口13的描述信息', '请求示例13', '请求头13',
        '响应头13', 1, '2022-01-13 10:00:00', '2022-01-13 10:00:00', 0),
       ('接口14', 'http://example.com/api/14', 14, 'POST', 'param1=value1&param2=value2', '接口14的描述信息', '请求示例14', '请求头14',
        '响应头14', 1, '2022-01-14 10:00:00', '2022-01-14 10:00:00', 0),
       ('接口15', 'http://example.com/api/15', 15, 'GET', 'param1=value1&param2=value2', '接口15的描述信息', '请求示例15', '请求头15',
        '响应头15', 1, '2022-01-15 10:00:00', '2022-01-15 10:00:00', 0),
       ('接口16', 'http://example.com/api/16', 16, 'POST', 'param1=value1&param2=value2', '接口16的描述信息', '请求示例16', '请求头16',
        '响应头16', 1, '2022-01-16 10:00:00', '2022-01-16 10:00:00', 0),
       ('接口17', 'http://example.com/api/17', 17, 'GET', 'param1=value1&param2=value2', '接口17的描述信息', '请求示例17', '请求头17',
        '响应头17', 1, '2022-01-17 10:00:00', '2022-01-17 10:00:00', 0),
       ('接口18', 'http://example.com/api/18', 18, 'POST', 'param1=value1&param2=value2', '接口18的描述信息', '请求示例18', '请求头18',
        '响应头18', 1, '2022-01-18 10:00:00', '2022-01-18 10:00:00', 0),
       ('接口19', 'http://example.com/api/19', 19, 'GET', 'param1=value1&param2=value2', '接口19的描述信息', '请求示例19', '请求头19',
        '响应头19', 1, '2022-01-19 10:00:00', '2022-01-19 10:00:00', 0),
       ('接口20', 'http://example.com/api/20', 20, 'POST', 'param1=value1&param2=value2', '接口20的描述信息', '请求示例20', '请求头20',
        '响应头20', 1, '2022-01-20 10:00:00', '2022-01-20 10:00:00', 0);