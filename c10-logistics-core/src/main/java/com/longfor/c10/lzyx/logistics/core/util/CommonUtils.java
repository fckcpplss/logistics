package com.longfor.c10.lzyx.logistics.core.util;

import cn.hutool.core.lang.Matcher;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.pattern.matcher.AlwaysTrueValueMatcher;
import cn.hutool.cron.pattern.matcher.ValueMatcherBuilder;
import cn.hutool.cron.pattern.parser.ValueParser;
import com.longfor.c2.starter.common.util.JsonUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 物流通用工具类
 * @author zhaoyl
 * @date 2022/4/14 下午6:32
 * @since 1.0
 */
public class CommonUtils {
    public static String checkDateShortToLong(String date,boolean isBegin){
        return checkDateShort(date) ? isBegin ? date + " 00:00:00" : date + " 23:59:59"  : date;
    }
    public static boolean checkDateShort(String date){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try{
            formatter.parse(date);
            return true;
        }catch(Exception e){
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(checkDateShortToLong("2021-01-03",true));
        boolean b = StrUtil.containsOnly("******1", '*');
        System.out.println(b);
    }
}
