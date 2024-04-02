<p align="center">
    <img src=https://img.qimuu.icu/typory/logo.gif width=188/>
</p>
<h1 align="center">Qi-API 接口开放平台</h1>
<p align="center"><strong>Qi-API 接口开放平台是一个为用户和开发者提供全面API接口调用服务的平台 🛠</strong></p>
<div align="center">
<a target="_blank" href="https://github.com/qimu666/qi-api">
    <img alt="" src="https://github.com/qimu666/qi-api/badge/star.svg?theme=gvp"/>
</a>
<a target="_blank" href="https://github.com/qimu666/qi-api">
    <img alt="" src="https://img.shields.io/github/stars/qimu666/qi-api.svg?style=social&label=Stars"/>
</a>
    <img alt="Maven" src="https://raster.shields.io/badge/Maven-3.8.1-red.svg"/>
<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
        <img alt="" src="https://img.shields.io/badge/JDK-1.8+-green.svg"/>
</a>
    <img alt="SpringBoot" src="https://raster.shields.io/badge/SpringBoot-2.7+-green.svg"/>
<a href="https://github.com/qimu666/qi-api" target="_blank">
    <img src='https://img.shields.io/github/forks/qimu666/qi-api' alt='GitHub forks' class="no-zoom">
</a>
<a href="https://github.com/qimu666/qi-api" target="_blank"><img src='https://img.shields.io/github/stars/qimu666/qi-api' alt='GitHub stars' class="no-zoom">
</a>
</div>

## 项目介绍 🙋



**😀 作为用户您可以通过注册登录账户，获取接口调用权限，并根据自己的需求浏览和选择适合的接口。您可以在线进行接口调试，快速验证接口的功能和效果。** 

**💻 作为开发者 我们提供了[客户端SDK: Qi-API-SDK](https://github.com/qimu666/qi-api-sdk)， 通过[开发者凭证](https://api.qimuu.icu/account/center)即可将轻松集成接口到您的项目中，实现更高效的开发和调用。** 

**🤝 您可以将自己的接口接入到Qi-API 接口开放平台平台上，并发布给其他用户使用。 您可以管理和各个接口，以便更好地分析和优化接口性能。** 

**👌 我们还提供了[开发者在线文档](https://doc.qimuu.icu/)和技术支持，帮助您快速接入和发布接口。**

 **🏁 无论您是用户还是开发者，Qi-API 接口开放平台都致力于提供稳定、安全、高效的接口调用服务，帮助您实现更快速、便捷的开发和调用体验。**

## 网站导航 🧭

- [**Qi-API 后端 🏘️**](https://github.com/qimu666/qi-api)
- [**Qi-API 前端 🏘**️](https://github.com/qimu666/qi-api-frontend)

-  **[Qi-API-SDK](https://github.com/qimu666/qi-api-sdk)** 🛠

-  **[Qi-API 接口开放平台 🔗](https://api.qimuu.icu/)**

-  **[Qi-API-DOC 开发者文档 📖](https://doc.qimuu.icu/)**
-  **[Qi-API-SDK-demo ✔️](https://github.com/qimu666/qi-api-sdk-demo/blob/master/src/main/java/icu/qimuu/qiapisdkdemo/controller/InvokeController.java)**


## 目录结构 📑


| 目录                                                     | 描述               |
|--------------------------------------------------------| ------------------ |
| **🏘️ [qi-api-backend](./qi-api-backend)**             | Qi-API后端服务模块 |
| **🏘️ [qi-api-common](./qi-api-common)**               | 公共服务模块       |
| **🕸️ [qi-api-gateway](./qi-api-gateway)**             | 网关模块           |
| **🔗 [qi-api-interface](./qi-api-interface)**          | 接口模块           |
| **🛠 [qi-qpi-sdk](https://github.com/qimu666/qi-api-sdk)** | 开发者调用sdk      |
| **📘 [qi-api-doc](https://doc.qimuu.icu/)**            | 接口在线文档       |
| **✔️ [Qi-API-SDK-Demo](https://github.com/qimu666/qi-api-sdk-demo/blob/master/src/main/java/icu/qimuu/qiapisdkdemo/controller/InvokeController.java)** | sdk调用Demo |

## 项目流程 🗺️

![QiAPI 接口开放平台](https://img.qimuu.icu/typory/QiAPI%2520%25E6%258E%25A5%25E5%258F%25A3%25E5%25BC%2580%25E6%2594%25BE%25E5%25B9%25B3%25E5%258F%25B0.png)

## 快速启动 🚀

### 前端

环境要求：Node.js >= 16

安装依赖：

```bash
yarn or npm install
```

启动：

```bash
yarn run dev or npm run start:dev
```

部署：

```bash
yarn build or npm run build
```

### 后端

执行sql目录下ddl.sql

## 项目选型 🎯

### **后端**

- Spring Boot 2.7.0
- Spring MVC
- MySQL 数据库
- 腾讯云COS存储
- Dubbo 分布式（RPC、Nacos）
- Spring Cloud Gateway 微服务网关
- API 签名认证（Http 调用）
- IJPay-AliPay  支付宝支付
- WeiXin-Java-Pay  微信支付
- Swagger + Knife4j 接口文档
- Spring Boot Starter（SDK 开发）
- Jakarta.Mail 邮箱通知、验证码
- Spring Session Redis 分布式登录
- Apache Commons Lang3 工具类
- MyBatis-Plus 及 MyBatis X 自动生成
- Hutool、Apache Common Utils、Gson 等工具库

### 前端

- React 18

- Ant Design Pro 5.x 脚手架

- Ant Design & Procomponents 组件库

- Umi 4 前端框架

- OpenAPI 前端代码生成

  

## 功能介绍 📋

`坤币`即积分，用于平台接口调用。

|                          **功能**                           | 游客 | **普通用户** | **管理员** |
| ----------------------------------------------------- |--------------|-----|-----|
| [**Qi-API-SDK**](https://github.com/qimu666/qi-api-sdk)使用 | ✅ | ✅ |     ✅      |
|        **[开发者API在线文档](http://doc.qimuu.icu)**        | ✅ | ✅ |     ✅      |
|                     邀请好友注册得坤币                      | ❌ | ✅ |     ✅      |
|                    切换主题、深色、暗色                     | ✅ | ✅ | ✅ |
|                       微信支付宝付款                        | ❌ | ✅ | ✅ |
|                        在线调试接口                         | ❌ | ✅ | ✅ |
|                       每日签到得坤币                        | ❌ | ✅ | ✅ |
|                 接口大厅搜索接口、浏览接口                  | ✅ | ❌ | ✅ |
|                     邮箱验证码登录注册                      | ✅ | ✅ | ✅ |
|                          钱包充值                           | ❌ | ❌ | ✅ |
|                     支付成功邮箱通知(需要绑定邮箱)                     | ❌ | ✅ | ✅ |
|                          更新头像                           | ❌ | ✅ | ✅ |
|                    绑定、换绑、解绑邮箱                     | ❌ | ✅ | ✅ |
|                          取消订单、删除订单                          | ❌ | ✅ | ✅ |
|                    商品管理、上线、下架                     | ❌ | ❌ |✅|
|                    用户管理、封号解封等                     | ❌ | ❌ | ✅ |
|                接口管理、接口发布审核、下架                 | ❌ | ❌ | ✅ |
|                            退款                             | ❌ | ❌| ❌ |

## 功能展示 ✨

### 首页

![index](https://img.qimuu.icu/typory/index.png)

### 接口广场

![interfaceSquare](https://img.qimuu.icu/typory/interfaceSquare.png)

### 开发者在线文档

![api](https://img.qimuu.icu/typory/api.png)

![api2](https://img.qimuu.icu/typory/api2.png)

### 接口描述

#### **在线API**

![interfaceinfo-api](https://img.qimuu.icu/typory/interfaceinfo-api.png)

#### 在线调试工具![interfaceinfo-tools](https://img.qimuu.icu/typory/interfaceinfo-tools.png)

#### **错误码参考**![interfaceinfo-errorcode](https://img.qimuu.icu/typory/interfaceinfo-errorcode.png)

#### **接口调用代码示例**![interfaceinfo-sampleCode](https://img.qimuu.icu/typory/interfaceinfo-sampleCode.png)

### 管理页

#### 用户管理

![admin-userManagement](https://img.qimuu.icu/typory/admin-userManagement.png)

#### 商品管理![admin-productManagement](https://img.qimuu.icu/typory/admin-productManagement.png)

#### 接口管理![admin-interfaceManagement](https://img.qimuu.icu/typory/admin-interfaceManagement.png)

#### 动态更新请求响应参数![dynamicRequestParameters](https://img.qimuu.icu/typory/dynamicRequestParameters.png)


### 积分商城

![pointPurchase](https://img.qimuu.icu/typory/pointPurchase.png)

### 订单支付![pay](https://img.qimuu.icu/typory/pay.png)

### 个人信息

#### 信息展示

![userinfo](https://img.qimuu.icu/typory/userinfo.png)

#### 每日签到

##### 签到成功![successfullySignedIn](https://img.qimuu.icu/typory/successfullySignedIn.png)

##### 签到失败![errorfullySignedIn](https://img.qimuu.icu/typory/errorfullySignedIn.png)

### 好友邀请

#### **发送邀请**![Invitefriends](https://img.qimuu.icu/typory/Invitefriends.png)

#### **接收邀请**![registerThroughInvitationCode](https://img.qimuu.icu/typory/registerThroughInvitationCode.png)

### 登录/注册![login](https://img.qimuu.icu/typory/login.png)

![register](https://img.qimuu.icu/typory/register.png)

### 订单管理

- **我的订单**![orderinfo](https://img.qimuu.icu/typory/orderinfo.png)

- **详细订单**![orderDetails](https://img.qimuu.icu/typory/orderDetails.png)
### 主题切换

#### 深色主题![darkTheme](https://img.qimuu.icu/typory/darkTheme.png)

#### 浅色主题![index](https://img.qimuu.icu/typory/index.png)
