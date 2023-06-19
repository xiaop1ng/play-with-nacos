package org.play;

import org.play.entity.Goods;
import org.play.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/goods")
@SpringBootApplication(scanBasePackages = "org.play")
// 开启服务注册发现功能
@EnableDiscoveryClient
// 开启分布式事务功能
@EnableTransactionManagement
public class GoodsDbApp {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/get")
    public Goods get() {
        return goodsService.findById(1L).get();
    }

    @GetMapping("/sell")
    public synchronized boolean sell() {
        Goods goods = goodsService.findById(1L).get();
        if ( goods.getStockNumber() <=0 ) return false;
        // 扣减库存：简单的用 synchronized 锁库存，不想写 SQL
        goods.setStockNumber(goods.getStockNumber() - 1);
        goodsService.save(goods);
        return true;
    }


    public static void main(String[] args) {
        SpringApplication.run(GoodsDbApp.class, args);
    }

}
