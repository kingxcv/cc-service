package com.cccz.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("t_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 订单状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-已退款 */
    private Integer orderStatus;

    /** 支付状态：0-未支付 1-支付中 2-已支付 3-支付失败 */
    private Integer payStatus;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 优惠金额 */
    private BigDecimal discountAmount;

    /** 实付金额 */
    private BigDecimal payAmount;

    /** 支付方式：1-微信 2-支付宝 3-银行卡 */
    private Integer payChannel;

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

    /** 乐观锁版本号 */
    @Version
    private Integer version;

    /** 逻辑删除：0-未删除 1-已删除 */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
