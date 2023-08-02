package com.sky.task;

import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import java.util.List;

/**
 * 定时任务类
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ? ")
    //每分钟触发一次
    public void processTimeOutOrder(){
        log.info("定时处理超时订单{}", LocalDateTime.now());
        //查询待付款状态的当前时间-15分钟<15分钟

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList=orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,time);

        if (!CollectionUtils.isEmpty(ordersList)){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }

    }

    /**
     * 处理一直在派送的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    //每天凌晨一点触发
    public void processDeliveryOrder(){
        log.info("定时处理一直派送中的订单{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if (!CollectionUtils.isEmpty(ordersList)){
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
