
模块

- server-order 订单服务
- server-goods 商品服务
- server-zuul 网关


## 版本选择

适配老版本
当前 SpringBoot 版本：2.0.4.RELEASE

[官方](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)建议版本
Spring Cloud Version
> Spring Cloud Finchley

Spring Cloud Alibaba Version
> 2.0.4.RELEASE(停止维护，建议升级)

> 测试 2.0.4 版本依赖 `InstancePreRegisteredEvent` 为 Spring 2.0.4 之后的版本中加入的类，应用启动会报找不到该类，可以降版本到 2.0.2

> 2.0.2.RELEASE

Nacos Version
> 1.4.1


## Nacos Server

https://github.com/alibaba/nacos/releases/download/1.4.1/nacos-server-1.4.1.zip

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
            <version>2.0.4.RELEASE</version>
        </dependency>
        
        <!-- nacos服务发现 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <version>2.0.4.RELEASE</version>
        </dependency>
    
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Finchley.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

## 使用 Nacos Config

Nacos 配置管理


