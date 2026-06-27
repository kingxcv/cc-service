package com.cccz.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cccz.order.vo.OrderCancelReq;
import com.cccz.order.vo.OrderCreateReq;
import com.cccz.order.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     */
    OrderVO createOrder(OrderCreateReq req);

    /**
     * 根据订单ID查询
     */
    OrderVO getOrderById(Long orderId);

    /**
     * 根据订单号查询
     */
    OrderVO getOrderByNo(String orderNo);

    /**
     * 分页查询用户订单
     */
    IPage<OrderVO> pageUserOrders(Long userId, Integer orderStatus, int pageNum, int pageSize);

    /**
     * 取消订单
     */
    void cancelOrder(OrderCancelReq req);

    /**
     * 支付订单
     */
    void payOrder(Long orderId, String payTransactionNo, Integer payChannel);

    /**
     * 发货
     */
    void shipOrder(Long orderId, String deliveryCompany, String deliveryNo);

    /**
     * 完成订单
     */
    void finishOrder(Long orderId);
}
