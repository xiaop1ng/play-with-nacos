package org.play.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@FeignClient("goods")
public interface GoodsService {

    @GetMapping("/goods/price")
    public Long getPrice();
}
