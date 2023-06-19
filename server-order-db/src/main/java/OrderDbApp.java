import io.seata.spring.annotation.GlobalTransactional;
import org.play.dao.OrderService;
import org.play.entity.Order;
import org.play.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@SpringBootApplication(scanBasePackages = "org.play")
@EnableJpaRepositories(basePackages = "org.play.dao")
@EntityScan(basePackages = "org.play.entity")
// 开启服务注册发现功能
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.play.service")
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
        order.setCreateTime(System.currentTimeMillis());
        Order insert = orderService.save(order);
        boolean success = goodsService.sell();
        if (!success) throw new RuntimeException("商品已售罄，下单失败");
        return insert;
    }




    public static void main(String[] args) {
        SpringApplication.run(OrderDbApp.class, args);
    }

}
