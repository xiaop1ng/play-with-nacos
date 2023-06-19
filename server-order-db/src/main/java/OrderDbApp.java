//import io.seata.spring.annotation.GlobalTransactional;
import io.seata.spring.annotation.GlobalTransactional;
import org.play.entity.Order;
import org.play.service.GoodsService;
import org.play.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-db")
@SpringBootApplication(scanBasePackages = "org.play")
// 开启服务注册发现功能
@EnableDiscoveryClient
// 开启 feign 远程方法调用
@EnableFeignClients
// 启用全局事务管理
@EnableTransactionManagement
public class OrderDbApp {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;


    // seata AT 分布式事务
    @GlobalTransactional
    @GetMapping("/create")
    public Order create() {
        Order order = new Order();
        Order insert = orderService.save(order);
        boolean success = goodsService.sell();
        if (!success) throw new RuntimeException("商品已售罄，下单失败");
        return order;
    }




    public static void main(String[] args) {
        SpringApplication.run(OrderDbApp.class, args);
    }

}
