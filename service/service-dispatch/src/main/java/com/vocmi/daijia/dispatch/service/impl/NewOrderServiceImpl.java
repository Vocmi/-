package com.vocmi.daijia.dispatch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vocmi.daijia.common.constant.RedisConstant;
import com.vocmi.daijia.common.result.Result;
import com.vocmi.daijia.dispatch.mapper.OrderJobMapper;
import com.vocmi.daijia.dispatch.service.NewOrderService;
import com.vocmi.daijia.dispatch.xxl.client.XxlJobClient;
import com.vocmi.daijia.map.client.LocationFeignClient;
import com.vocmi.daijia.model.entity.dispatch.OrderJob;
import com.vocmi.daijia.model.enums.OrderStatus;
import com.vocmi.daijia.model.form.map.SearchNearByDriverForm;
import com.vocmi.daijia.model.vo.dispatch.NewOrderTaskVo;
import com.vocmi.daijia.model.vo.map.NearByDriverVo;
import com.vocmi.daijia.model.vo.order.NewOrderDataVo;
import com.vocmi.daijia.order.client.OrderInfoFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class NewOrderServiceImpl implements NewOrderService {

    @Resource
    private OrderJobMapper orderJobMapper;

    @Resource
    private XxlJobClient xxlJobClient;

    @Resource
    private LocationFeignClient locationFeignClient;

    @Resource
    private OrderInfoFeignClient orderInfoFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Long addAndStartTask(NewOrderTaskVo newOrderTaskVo) {
        LambdaQueryWrapper<OrderJob> orderJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        OrderJob orderJob = orderJobMapper.selectOne(orderJobLambdaQueryWrapper.eq(OrderJob::getOrderId, newOrderTaskVo.getOrderId()));

        if (BeanUtil.isEmpty(orderJob)) {
            //创建并启动任务调度
            Long jobId = xxlJobClient.addAndStart("newOrderTaskHandler", "", "0 0/1 * * * ?", "新订单任务,订单id：" + newOrderTaskVo.getOrderId());

            // TODO 可能存在还没入库就执行的风险
            //记录订单与任务的关联信息
            orderJob = new OrderJob();
            orderJob.setOrderId(newOrderTaskVo.getOrderId());
            orderJob.setJobId(jobId);
            orderJob.setParameter(JSONObject.toJSONString(newOrderTaskVo));
            orderJobMapper.insert(orderJob);
        }

        return orderJob.getJobId();
    }

    @Override
    public void executeTask(long jobId) {
        LambdaQueryWrapper<OrderJob> orderJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        OrderJob orderJob = orderJobMapper.selectOne(orderJobLambdaQueryWrapper.eq(OrderJob::getJobId, jobId));
        if (BeanUtil.isEmpty(orderJob)) {
            return;
        }

        String parameter = orderJob.getParameter();
        NewOrderTaskVo newOrderTaskVo = JSONObject.parseObject(parameter, NewOrderTaskVo.class);

        Long orderId = newOrderTaskVo.getOrderId();
        Integer data = orderInfoFeignClient.getOrderStatus(orderId).getData();
        if (data.intValue() != OrderStatus.WAITING_ACCEPT.getStatus().intValue()) {
            //停止任务调度
            xxlJobClient.removeJob(jobId);
            return;
        }

        //搜索满足条件的司机
        SearchNearByDriverForm searchNearByDriverForm = new SearchNearByDriverForm();
        searchNearByDriverForm.setLatitude(newOrderTaskVo.getStartPointLatitude());
        searchNearByDriverForm.setLongitude(newOrderTaskVo.getStartPointLongitude());
        searchNearByDriverForm.setMileageDistance(newOrderTaskVo.getExpectDistance());
        List<NearByDriverVo> nearByDriverVoList = locationFeignClient.searchNearByDriver(searchNearByDriverForm).getData();

        nearByDriverVoList.forEach(driver -> {
            String repeatKey = RedisConstant.DRIVER_ORDER_REPEAT_LIST + newOrderTaskVo.getOrderId();
            boolean isMember = redisTemplate.opsForSet().isMember(repeatKey, driver.getDriverId());//用set保证司机不重复
            if (!isMember) {
                //把订单信息推送给满足条件的司机，并设置过期时间为15分钟
                redisTemplate.opsForSet().add(repeatKey, driver.getDriverId());
                redisTemplate.expire(repeatKey, RedisConstant.DRIVER_ORDER_REPEAT_LIST_EXPIRES_TIME, TimeUnit.MINUTES);

                //新订单保存司机的临时队列，Redis里面List集合
                NewOrderDataVo newOrderDataVo = BeanUtil.copyProperties(newOrderTaskVo, NewOrderDataVo.class);
                newOrderDataVo.setDistance(driver.getDistance());
                String key = RedisConstant.DRIVER_ORDER_TEMP_LIST + driver.getDriverId();
                redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(newOrderDataVo));

                //过期时间：1分钟，1分钟未消费，自动过期
                //注：司机端开启接单，前端每5秒（远小于1分钟）拉取1次“司机临时队列”里面的新订单消息
                redisTemplate.expire(key, RedisConstant.DRIVER_ORDER_TEMP_LIST_EXPIRES_TIME, TimeUnit.MINUTES);
                log.info("该新订单信息已放入司机临时队列: {}", JSON.toJSONString(newOrderDataVo));
            }
        });
    }

    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {
        List<NewOrderDataVo> list = new ArrayList<>();
        String key = RedisConstant.DRIVER_ORDER_TEMP_LIST + driverId;
        long size = redisTemplate.opsForList().size(key);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String content = (String) redisTemplate.opsForList().leftPop(key);
                NewOrderDataVo newOrderDataVo = JSONObject.parseObject(content, NewOrderDataVo.class);
                list.add(newOrderDataVo);
            }
        }
        return list;
    }

    @Override
    public Boolean clearNewOrderQueueData(Long driverId) {
        String key = RedisConstant.DRIVER_ORDER_TEMP_LIST + driverId;
        //直接删除，司机开启服务后，有新订单会自动创建容器
        redisTemplate.delete(key);
        return true;
    }
}
