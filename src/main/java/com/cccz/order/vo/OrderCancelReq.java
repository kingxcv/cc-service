package com.cccz.order.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 取消订单入参
 */
@Data
public class OrderCancelReq {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;
}
