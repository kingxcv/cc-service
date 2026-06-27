package com.cccz.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单出参 VO
 */
@Data
public class OrderVO {

    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 订单状态 */
    private Integer orderStatus;
    private String orderStatusDesc;

    /** 支付状态 */
    private Integer payStatus;
    private String payStatusDesc;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 优惠金额 */
    private BigDecimal discountAmount;

    /** 实付金额 */
    private BigDecimal payAmount;

    /** 支付方式 */
    private Integer payChannel;
    private String payChannelDesc;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 支付流水号 */
    private String payTransactionNo;

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人手机号 */
    private String receiverPhone;

    /** 收货省 */
    private String receiverProvince;

    /** 收货市 */
    private String receiverCity;

    /** 收货区 */
    private String receiverDistrict;

    /** 收货详细地址 */
    private String receiverDetail;

    /** 物流公司 */
    private String deliveryCompany;

    /** 物流单号 */
    private String deliveryNo;

    /** 发货时间 */
    private LocalDateTime deliveryTime;

    /** 完成时间 */
    private LocalDateTime finishTime;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    /** 取消原因 */
    private String cancelReason;

    /** 订单备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
