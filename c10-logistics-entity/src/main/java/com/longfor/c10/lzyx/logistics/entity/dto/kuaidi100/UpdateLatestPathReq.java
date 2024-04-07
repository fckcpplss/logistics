package com.longfor.c10.lzyx.logistics.entity.dto.kuaidi100;

import lombok.Data;

import java.util.List;

@Data
public class UpdateLatestPathReq {
    private String status;

    private String billstatus;

    private String message;

    private String autoCheck;

    private String comOld;

    private String comNew;

    private LastResult lastResult;

    private DestResult destResult;

    @lombok.Data
    public static class LastResult {
        private String message;

        private String state;

        private String status;

        private String condition;

        private String ischeck;

        private String com;

        private String nu;

        private List<Data> data;
    }

    @lombok.Data
    public static class DestResult {
        private String message;

        private String state;

        private String status;

        private String condition;

        private String ischeck;

        private String com;

        private String nu;

        private List<Data> data;


    }

    @lombok.Data
    public static class Data {
        private String context;

        private String time;

        private String ftime;

        private String status;

        private String areaCode;

        private String areaName;

    }
}
