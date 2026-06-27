package com.cccz.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
@AllArgsConstructor
public enum PayStatus {

    UNPAID(0, "未支付"),
    PAYING(1, "支付中"),
    PAID(2, "已支付"),
    PAY_FAILED(3, "支付失败");

    private final Integer code;
    private final String desc;

    public static PayStatus of(Integer code) {
        if (code == null) {
            return null;
        }
        for (PayStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
