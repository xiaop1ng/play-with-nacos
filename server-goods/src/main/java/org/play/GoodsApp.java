package org.play;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/goods")
@SpringBootApplication
// 开启服务注册发现功能
@EnableDiscoveryClient
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
