package com.longfor.c10.lzyx.logistics.core.controller.open;

import com.longfor.c10.lzyx.logistics.core.service.ILogisticsCommonBaseAddressService;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsAddressDetailReqData;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsAddressInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.LogisticsAddressListReq;
import com.longfor.c10.lzyx.logistics.entity.dto.auth.UserTokenInfo;
import com.longfor.c10.lzyx.logistics.entity.dto.open.LogisticsAddressByChannelAndUserListReqData;
import com.longfor.c10.lzyx.logistics.entity.exception.BusinessException;
import com.longfor.c2.starter.data.domain.page.PageInfo;
import com.longfor.c2.starter.data.domain.request.Request;
import com.longfor.c2.starter.data.domain.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 用户地址
 * @author zhaoyl
 * @date 2022/3/23 下午1:40
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/lzyx/logistics/open/address")
public class LogisticsOpenAddressController {

    @Autowired
    private ILogisticsCommonBaseAddressService logisticsCommonBaseAddressService;


    /**
     * 查询用户地址集合
     */
    @PostMapping(path = "/user/list")
    Response<List<LogisticsAddressInfo>> userlist(@RequestBody @Valid Request<LogisticsAddressByChannelAndUserListReqData> request){
        LogisticsAddressByChannelAndUserListReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        LogisticsAddressListReq listReq = new LogisticsAddressListReq();
        UserTokenInfo userTokenInfo = new UserTokenInfo();
        userTokenInfo.setLmId(req.getUserId());
        req.setUserTokenInfo(userTokenInfo);
        return logisticsCommonBaseAddressService.list(listReq,new PageInfo(){{
            setPageSize(1);
            setPageSize(Integer.MAX_VALUE);
        }});
    }

    /**
     * 查询用户地址集合
     */
    @PostMapping(path = "/user/detail")
    Response<LogisticsAddressInfo> userDetail(@RequestBody @Valid Request<LogisticsAddressDetailReqData> request){
        LogisticsAddressDetailReqData req = Optional.ofNullable(request)
                .map(Request::getData)
                .orElseThrow(() -> new BusinessException("请求参数不能为空"));
        UserTokenInfo userTokenInfo = new UserTokenInfo();
        userTokenInfo.setLmId("-1");
        req.setUserTokenInfo(userTokenInfo);
        return logisticsCommonBaseAddressService.detail(req);
    }

}
