package com.longfor.c10.lzyx.logistics.core.schedule;

import com.longfor.c10.lzyx.logistics.core.service.schedule.IPickUpNoticeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@JobHandler(value = "pickUpNoticeUserJobHandler")
@Component
public class PickUpNoticeUserJobHandler extends IJobHandler {

    @Resource
    private IPickUpNoticeService pickUpNoticeService;

    /**
     * @description 自提提醒-用户
     */
    @Override
    public ReturnT<String> execute(String param) {
        log.info("自提提醒-用户定时任务处理");
        try {
            pickUpNoticeService.noticeUser();
        } catch (Exception e) {
            log.error("错误信息{}", e);
            return FAIL;
        }
        return SUCCESS;
    }

}
