package com.vocmi.daijia.dispatch.xxl.job;

import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.vocmi.daijia.dispatch.mapper.XxlJobLogMapper;
import com.vocmi.daijia.dispatch.service.NewOrderService;
import com.vocmi.daijia.model.entity.dispatch.XxlJobLog;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author vocmi
 * @Email 2686782542@qq.com
 * @Date 2024-08-18
 */
@Component
@Slf4j
public class JobHandler {
    @Resource
    private XxlJobLogMapper xxlJobLogMapper;

    @Resource
    private NewOrderService newOrderService;

    @XxlJob("newOrderTaskHandler")
    public void newOrderTaskHandler() {
        log.info("新订单调度任务：{}", XxlJobHelper.getJobId());

        //记录定时任务相关的日志信息
        //封装日志对象
        XxlJobLog xxlJobLog = new XxlJobLog();
        xxlJobLog.setJobId(XxlJobHelper.getJobId());
        long startTime = System.currentTimeMillis();
        try {
            //执行任务
            newOrderService.executeTask(XxlJobHelper.getJobId());

            xxlJobLog.setStatus(1);//成功
        } catch (Exception e) {
            xxlJobLog.setStatus(0);//失败
            xxlJobLog.setError(ExceptionUtil.getAllExceptionMsg(e));
            log.error("定时任务执行失败，任务id为：{}", XxlJobHelper.getJobId());
            e.printStackTrace();
        } finally {
            //耗时
            int times = (int) (System.currentTimeMillis() - startTime);
            xxlJobLog.setTimes(times);
            xxlJobLogMapper.insert(xxlJobLog);
        }
    }
}
