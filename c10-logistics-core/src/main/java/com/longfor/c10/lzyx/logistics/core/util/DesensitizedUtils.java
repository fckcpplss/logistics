package com.longfor.c10.lzyx.logistics.core.util;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhaoyl
 * @date 2022/2/21 上午10:32
 * @since 1.0
 */
public class DesensitizedUtils {
    public static String maskValue(String origin, int prefixNoMaskLen, int suffixNoMaskLen, String maskStr) {
        if (origin == null) {
            return origin;
        } else {
            int length = origin.length();
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < length; ++i) {
                if (i < prefixNoMaskLen) {
                    sb.append(origin.charAt(i));
                } else if (i > length - suffixNoMaskLen - 1) {
                    sb.append(origin.charAt(i));
                } else {
                    sb.append(maskStr);
                }
            }

            return sb.toString();
        }
    }

    public static String maskChineseName(String fullName) {
        if (fullName == null) {
            return fullName;
        } else {
            return fullName.length() > 2 ? maskValue(fullName, 1, 1, "*") : maskValue(fullName, 1, 0, "*");
        }
    }

    public static String maskIdCardNum(String id) {
        return maskValue(id, 6, 4, "*");
    }

    public static String maskFixedPhone(String num) {
        return maskValue(num, 0, 4, "*");
    }

    public static String maskMobilePhone(String num) {
        return maskValue(num, 3, 4, "*");
    }

    public static String maskAddress(String address) {
        return maskValue(address, 6, 0, "*");
    }

    public static String maskEmail(String email) {
        if (email == null) {
            return email;
        } else {
            int index = StringUtils.indexOf(email, "@");
            if (index <= 1) {
                return email;
            } else {
                String temp = maskValue(email.substring(0, index), 1, 0, "*");
                return temp == null ? email : temp.concat(email.substring(index));
            }
        }
    }

    public static String maskBankCard(String cardNum) {
        return maskValue(cardNum, 6, 4, "*");
    }

    public static String maskPassword(String password) {
        return password == null ? null : "******";
    }
}
