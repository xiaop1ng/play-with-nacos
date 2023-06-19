package org.play.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@FeignClient("goods-db")
public interface GoodsService {

    /**
     * 扣减库存
     * @return
     */
    @GetMapping("/goods/sell")
    public boolean sell();

    @GetMapping("/goods/price")
    public Long getPrice();
}
