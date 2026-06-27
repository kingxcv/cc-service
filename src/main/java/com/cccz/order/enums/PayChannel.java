package com.cccz.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
@AllArgsConstructor
public enum PayChannel {

    WECHAT(1, "微信"),
    ALIPAY(2, "支付宝"),
    BANK_CARD(3, "银行卡");

    private final Integer code;
    private final String desc;

    public static PayChannel of(Integer code) {
        if (code == null) {
            return null;
        }
        for (PayChannel channel : values()) {
            if (channel.code.equals(code)) {
                return channel;
            }
        }
        return null;
    }
}
