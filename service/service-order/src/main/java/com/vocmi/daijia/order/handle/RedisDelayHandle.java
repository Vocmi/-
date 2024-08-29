package com.vocmi.daijia.order.handle;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.vocmi.daijia.order.service.OrderInfoService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * @Author vocmi
 * @Email 2686782542@qq.com
 * @Date 2024-08-28
 */
//监听延迟队列
@Component
public class RedisDelayHandle {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private OrderInfoService orderInfoService;

    //bean初始化依赖注入之后再执行下面代码
    @PostConstruct
    public void listener(){
        new Thread(()->{
            while (true){
                //获取延迟队列里面阻塞队列
                RBlockingQueue<String> blockingQueue = redissonClient.getBlockingQueue("queue_cancel");

                //从队列获取消息
                try {
                    String orderId = blockingQueue.take();

                    //取消订单
                    if(StringUtil.isNotBlank(orderId)) {
                        //调用方法取消订单
                        orderInfoService.orderCancel(Long.parseLong(orderId));
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
