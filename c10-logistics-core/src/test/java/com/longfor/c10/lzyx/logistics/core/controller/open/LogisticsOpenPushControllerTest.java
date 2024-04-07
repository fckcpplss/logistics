package com.longfor.c10.lzyx.logistics.core.controller.open;

import com.longfor.c10.lzyx.logistics.core.service.open.ILogisticsOpenPushService;
import com.longfor.c10.lzyx.logistics.entity.dto.open.JdRoutResponse;
import com.longfor.c10.lzyx.logistics.entity.dto.open.SFResponse;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(PowerMockRunner.class)
public class LogisticsOpenPushControllerTest {

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
    }
    @InjectMocks
    LogisticsOpenPushController logisticsOpenPushController;
    @Mock
    private ILogisticsOpenPushService logisticsOpenPushService;
    @Test
    public void testSfPush() {
        MutablePair<String, String> mutablePair = new MutablePair<>();
        MultiValueMap<String,String> request = new LinkedMultiValueMap<String, String>();
        request.add("1","test");
        Mockito.when(logisticsOpenPushService.sfPush(Mockito.any())).thenReturn(mutablePair);
        logisticsOpenPushController.sfPush(request);
        Assert.assertTrue(true);
    }
    @Test
    public void testSfStatePush() {
        MutablePair<String, String> mutablePair = new MutablePair<>();
        MultiValueMap<String,String> request = new LinkedMultiValueMap<String, String>();
        request.add("1","test");
        Mockito.when(logisticsOpenPushService.sfStatePush(Mockito.any())).thenReturn(mutablePair);
        logisticsOpenPushController.sfStatePush(request);
        Assert.assertTrue(true);
    }
    @Test
    public void testSfPushFreight() {
        MutablePair<String, String> mutablePair = new MutablePair<>();
        MultiValueMap<String,String> request = new LinkedMultiValueMap<String, String>();
        request.add("1","test");
        Mockito.when(logisticsOpenPushService.sfPushFreight(Mockito.any())).thenReturn(mutablePair);
        logisticsOpenPushController.sfPushFreight(request);
        Assert.assertTrue(true);
    }
    @Test
    public void testEBillBack() {
        SFResponse mutablePair = new SFResponse();
        MultiValueMap<String,String> request = new LinkedMultiValueMap<String, String>();
        request.add("1","test");
        Mockito.when(logisticsOpenPushService.eBillBack(Mockito.any())).thenReturn(mutablePair);
        logisticsOpenPushController.eBillBack(request);
        Assert.assertTrue(true);
    }
    @Test
    public void testJdPush() {
        JdRoutResponse jdRoutResponse = new JdRoutResponse();
        MultiValueMap<String,String> request = new LinkedMultiValueMap<String, String>();
        request.add("1","test");
        Mockito.when(logisticsOpenPushService.jdPush(request)).thenReturn(jdRoutResponse);
        logisticsOpenPushController.jdPush(request);
        Assert.assertTrue(true);
    }
}