package org.play;

import org.play.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@SpringBootApplication
// 开启服务注册发现功能
@EnableDiscoveryClient
// 开启 feign 远程方法调用
@EnableFeignClients
public class OrderApp {

    @Autowired
    GoodsService goodsService;
    @GetMapping("/create")
    public String create() {
        // 新建订单，获取 goods 服务的商品价格
        Long price = goodsService.getPrice();
        return "创建订单成功，订单金额：" + price;
    }

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }

}
