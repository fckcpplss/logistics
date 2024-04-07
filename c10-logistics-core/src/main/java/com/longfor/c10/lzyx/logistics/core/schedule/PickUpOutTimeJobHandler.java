package com.longfor.c10.lzyx.logistics.core.schedule;

import com.longfor.c10.lzyx.logistics.core.service.schedule.IPickUpOutTimeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@JobHandler(value = "pickUpOutTimeJobHandler")
@Component
public class PickUpOutTimeJobHandler extends IJobHandler {

    @Resource
    private IPickUpOutTimeService pickUpOutTimeService;

    /**
     * @description 核销超期处理
     */
    @Override
    public ReturnT<String> execute(String param) {
        log.info("核销超期处理定时任务处理");
        try {
            pickUpOutTimeService.dealPickUpOutTime();
        } catch (Exception e) {
            log.error("错误信息{}", e);
            return FAIL;
        }
        return SUCCESS;
    }

}
