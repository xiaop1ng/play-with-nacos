
模块

- server-order 订单服务
- server-goods 商品服务
- ~~server-zuul 网关~~
- server-gateway 网关
- server-order-db 订单服务(with seata AT mode)
- server-goods-db 商品服务(with seata AT mode)


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
- 分布式事务
- 流控

## 版本选择

[使用初始化工具选择版本](https://start.aliyun.com/)

JDK 版本 1.8

升级 SpringBoot 版本：2.7.6

[官方](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)建议版本
Spring Cloud Version
> 2021.0.5

Spring Cloud Alibaba Version
> 2021.0.5.0

Nacos Version
> 2.2.0

Seata Version
> 1.6.1

Sentinel Version
> 1.8.6

## Nacos Server

https://github.com/alibaba/nacos/releases/download/2.2.0/nacos-server-2.2.0.zip

## Seata Server

https://github.com/seata/seata/releases/download/v1.6.1/seata-server-1.6.1.zip

## Sentinel Dashboard

https://github.com/alibaba/Sentinel/releases

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

## 部署 seata 以 nacos 作为注册中心


配置 `./conf/application.yml` seata 部分
```
seata:
  config:
    # support: nacos, consul, apollo, zk, etcd3
    type: nacos
    nacos:
      # 服务地址
      server-addr: 127.0.0.1:8848
      group: DEFAULT_GROUP
      username: nacos
      password: nacos
      data-id: seata-dev.yaml
  registry:
    # support: nacos, eureka, redis, zk, consul, etcd3, sofa
    type: nacos
    nacos:
      application: seata
      # 服务地址
      server-addr: 127.0.0.1:8848
      group: DEFAULT_GROUP
      cluster: default
      username: nacos
      password: nacos
  store:
    # support: file 、 db 、 redis
    mode: db
    db:
      datasource: druid
      db-type: mysql
      driver-class-name: com.mysql.jdbc.Driver
      url: jdbc:mysql://127.0.0.1:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
      user: root
      password: 123456
      min-conn: 10
      max-conn: 100
      global-table: global_table
      branch-table: branch_table
      lock-table: lock_table
      distributed-lock-table: distributed_lock
      query-limit: 1000
      max-wait: 5000
```

启动
> sh seata-server.sh

控制台地址
http://127.0.0.1:7091/ 默认用户名密码均为 seata

## 启动 Sentinel

> java -Dserver.port=88 -Dcsp.sentinel.dashboard.server=localhost:88 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.6.jar



配置应用：
```yaml
spring:
    cloud:
        # 流控
        sentinel:
            transport:
                # 默认端口 8719
                port: 8719
                dashboard: localhost:88
```

@SentinelResource 注解配置资源，然后可以在控制台为资源配置流控规则；主要控制权在 sentinel-dashboard
流控规则添加快速失败

- 流控
- 降级
- 热点
- 授权

sentinel 规则持久化
可以选择 Nacos/Redis

文档地址 https://sentinelguard.io/zh-cn/docs/quick-start.html

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

- gateway-dev.yaml
```
spring:
  application:
    name: gateway
  cloud:
    gateway:
      discovery:
        locator:
          # 是否从注册中心读取服务，只要你的路径中以微服务的服务名称开头，都会自动转发到该服务上去
          enabled: true
          # eureka 中服务名默认为大写 服务名大写转小写
          lowerCaseServiceId: true
server:
  port: 8088
```

测试：
```
req: http://127.0.0.1:80/goods/price
res: 1499
req: http://127.0.0.1:8080/order/create
res: 创建订单成功，订单金额：1499
req: http://127.0.0.1:8088/goods/goods/price
res: 1499
req: http://127.0.0.1:8088/order/order/create
res: 创建订单成功，订单金额：1499
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

## nacos & seata 服务配置文件

- nacos/conf
- seata/conf
- sentinel/conf

