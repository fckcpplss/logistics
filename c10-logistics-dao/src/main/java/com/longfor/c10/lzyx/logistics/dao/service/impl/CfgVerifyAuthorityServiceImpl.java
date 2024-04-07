package com.longfor.c10.lzyx.logistics.dao.service.impl;

import com.longfor.c10.lzyx.logistics.entity.entity.CfgVerifyAuthority;
import com.longfor.c10.lzyx.logistics.dao.mapper.CfgVerifyAuthorityMapper;
import com.longfor.c10.lzyx.logistics.dao.service.ICfgVerifyAuthorityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfor.property.crypto.service.CryptoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 有权限核销人配置表 服务实现类
 * </p>
 *
 * @author liuxin41
 * @since 2022-04-15
 */
@Service
public class CfgVerifyAuthorityServiceImpl extends ServiceImpl<CfgVerifyAuthorityMapper, CfgVerifyAuthority> implements ICfgVerifyAuthorityService {
    @Resource
    private CryptoService cryptoService;

    public static void main(String[] args) {

    }
    public void test(){
    }

}
