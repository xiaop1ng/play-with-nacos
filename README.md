
模块

- server-order 订单服务
- server-goods 商品服务
- server-zuul 网关


## todo

- 环境搭建
- 配置中心
    - 模块引入依赖
    - 环境配置
    - 原配置文件迁移
- 注册中心
    - 模块引入依赖
    - 环境配置
    - 服务开启注册发现
    - 服务间调用方式变更
    - 服务间联调测试
    - 网关调用方式变更
    - 网关联调测试
- 适配 Oracle 数据源，编译适配 Oracle 包：nacos-server-oracle-1.2.1.zip

## 版本选择

当前 SpringBoot 版本：2.0.4.RELEASE

[官方](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)建议版本
Spring Cloud Version
> Spring Cloud Finchley

Spring Cloud Alibaba Version
> ~~2.0.4.RELEASE(停止维护，建议升级)~~

> 测试 2.0.4 版本依赖 `InstancePreRegisteredEvent` 为 SpringBoot 2.0.4 之后的版本中加入的类，应用启动会报找不到该类，可以降版本到 2.0.2

> 2.0.2.RELEASE

Nacos Version
> ~~1.4.1~~

> 1.2.1

## Nacos Server

~~https://github.com/alibaba/nacos/releases/download/1.4.1/nacos-server-1.4.1.zip~~

https://github.com/alibaba/nacos/releases/download/1.2.1/nacos-server-1.2.1.zip

## 集群部署 Nacos

https://nacos.io/zh-cn/docs/cluster-mode-quick-start.html

推荐集群部署模式，keepalive + nacos cluster(3+)，客户端使用 vip 访问 nacos cluster

1. 配置 `./conf/cluster.conf` (可以复制 cluster.conf.example 文件)

> 注意：使用真实的IP，不要使用 `127.0.0.1` `localhost`
```
172.16.0.208:18848
172.16.0.208:28848
172.16.0.208:8848
```

2. 初始化数据库、配置当前服务（修改启动端口、数据源等配置）
- `./conf/nacos-mysql.sql`
- `./conf/application.properties`

3. 集群模式启动

使用外置数据源
> sh startup.sh

使用内置数据源
> sh startup.sh -p embedded

单机模式启动
> sh startup.sh -m standalone

等待所有节点启动成功
> 2023-05-30 10:37:16,894 INFO Nacos started successfully in cluster mode. use external storage

访问 http://127.0.0.1:8848/nacos/#/login 账号密码默认均为 nacos
查看 集群管理 > 节点列表

## 添加配置

DataId:

${appName}-${profile}.${file-extension}

示例:
```
spring:
  profiles:
    active: dev
  application:
    name: goods
  cloud:
    nacos:
      config:
        server-addr: 127.0.0.1:8848
        file-extension: yaml
```
goods 应用对应 nacos 的 DataId 为 goods-dev.yaml


- goods-dev.yaml
```
goods:
    price: 1499
server:
    port: 80
```

- order-dev.yaml
```
server:
    port: 8080
```

- zuul-dev.yaml
```
server:
    port: 8088
CROS: true
zuul:
    host:
        connect-timeout-millis: 600000
        socket-timeout-millis: 600000
    prefix: /api
    routes:
        goods:
            # 以/api/goods/ 开头的请求都转发给goods服务
            service-id: goods
            path: /goods/** 
        order:
            # 以/api/order/ 开头的请求都转发给order服务
            service-id: order
            path: /order/**
```

测试：
```
req: http://127.0.0.1:80/goods/price
res: 1499
req: http://127.0.0.1:8080/order/create
res: 创建订单成功，订单金额：1499
req: http://127.0.0.1:8088/api/goods/goods/price
res: 1499
req: http://127.0.0.1:8088/api/order/order/create
res: 创建订单成功，订单金额：1499
```

## 添加依赖
```
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.4.RELEASE</version>
    </parent>
    
    <dependencies>
        <!-- nacos配置中心 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
        
        <!-- nacos服务发现 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
    
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.0.4.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Finchley.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>2.0.2.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

## 使用 Nacos Config

Nacos 配置管理

```
@RefreshScope
@RestController
@RequestMapping("/goods")
@SpringBootApplication
public class GoodsApp {

    @Value("${goods.price:100}")
    private Long price;

    @GetMapping("/price")
    public Long get() {
        // @RefreshScope 会刷新 price 的值
        return price;
    }


    public static void main(String[] args) {
        SpringApplication.run(GoodsApp.class, args);
    }

}
```

## 适配 Oracle

1. fork nacos 仓库并 clone
2. 切换分支 feature_multiple_datasource_support
3. 修改 nacos-all 和 nacos-config 依赖，更换为需要适配的驱动
4. 修改 nacos-config 主键生成策略（mysql 为自增长，Oracle 需要调整为序列）
5. 修改数据源配置，单机启动测试
6. 合并分支
7. 打包

