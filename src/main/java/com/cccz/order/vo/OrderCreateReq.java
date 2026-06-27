package com.cccz.order.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单入参
 */
@Data
public class OrderCreateReq {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0")
    private BigDecimal totalAmount;

    @NotNull(message = "优惠金额不能为空")
    private BigDecimal discountAmount;

    @NotNull(message = "实付金额不能为空")
    private BigDecimal payAmount;

    /** 支付方式 */
    private Integer payChannel;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人手机号不能为空")
    private String receiverPhone;

    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;

    @NotBlank(message = "收货详细地址不能为空")
    private String receiverDetail;

    /** 订单备注 */
    private String remark;
}
