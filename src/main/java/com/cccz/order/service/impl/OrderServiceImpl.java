package com.cccz.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cccz.order.dao.OrderMapper;
import com.cccz.order.entity.Order;
import com.cccz.order.enums.OrderStatus;
import com.cccz.order.enums.PayChannel;
import com.cccz.order.enums.PayStatus;
import com.cccz.order.service.OrderService;
import com.cccz.order.vo.OrderCancelReq;
import com.cccz.order.vo.OrderCreateReq;
import com.cccz.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateReq req) {
        Order order = new Order();
        BeanUtils.copyProperties(req, order);

        order.setOrderNo(generateOrderNo());
        order.setOrderStatus(OrderStatus.PENDING_PAY.getCode());
        order.setPayStatus(PayStatus.UNPAID.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);
        log.info("创建订单成功, orderNo={}, userId={}", order.getOrderNo(), order.getUserId());

        return convertToVO(order);
    }

    @Override
    public OrderVO getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            log.warn("订单不存在或已删除, orderId={}", orderId);
            return null;
        }
        return convertToVO(order);
    }

    @Override
    public OrderVO getOrderByNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            log.warn("订单不存在或已删除, orderNo={}", orderNo);
            return null;
        }
        return convertToVO(order);
    }

    @Override
    public IPage<OrderVO> pageUserOrders(Long userId, Integer orderStatus, int pageNum, int pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(orderStatus != null, Order::getOrderStatus, orderStatus)
                .orderByDesc(Order::getCreateTime);

        Page<Order> page = new Page<>(pageNum, pageSize);
        IPage<Order> orderPage = orderMapper.selectPage(page, wrapper);

        IPage<OrderVO> voPage = new Page<>(pageNum, pageSize, orderPage.getTotal());
        voPage.setRecords(orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(OrderCancelReq req) {
        Order order = orderMapper.selectById(req.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING_PAY.getCode()) {
            throw new RuntimeException("只有待支付订单才能取消");
        }

        order.setOrderStatus(OrderStatus.CANCELED.getCode());
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(req.getCancelReason());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单取消成功, orderId={}, reason={}", req.getOrderId(), req.getCancelReason());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, String payTransactionNo, Integer payChannel) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING_PAY.getCode()) {
            throw new RuntimeException("订单状态不允许支付");
        }

        order.setPayStatus(PayStatus.PAID.getCode());
        order.setOrderStatus(OrderStatus.PAID.getCode());
        order.setPayTransactionNo(payTransactionNo);
        order.setPayChannel(payChannel);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("支付成功, orderId={}, payChannel={}", orderId, payChannel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long orderId, String deliveryCompany, String deliveryNo) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != OrderStatus.PAID.getCode()) {
            throw new RuntimeException("只有已支付订单才能发货");
        }

        order.setOrderStatus(OrderStatus.SHIPPED.getCode());
        order.setDeliveryCompany(deliveryCompany);
        order.setDeliveryNo(deliveryNo);
        order.setDeliveryTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("发货成功, orderId={}, deliveryCompany={}, deliveryNo={}", orderId, deliveryCompany, deliveryNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != OrderStatus.SHIPPED.getCode()) {
            throw new RuntimeException("只有已发货订单才能完成");
        }

        order.setOrderStatus(OrderStatus.FINISHED.getCode());
        order.setFinishTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单完成, orderId={}", orderId);
    }

    // ---------- 私有辅助方法 ----------

    private String generateOrderNo() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "ORD" + System.currentTimeMillis() + uuid.substring(0, 8).toUpperCase();
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        OrderStatus orderStatus = OrderStatus.of(order.getOrderStatus());
        if (orderStatus != null) {
            vo.setOrderStatusDesc(orderStatus.getDesc());
        }
        PayStatus payStatus = PayStatus.of(order.getPayStatus());
        if (payStatus != null) {
            vo.setPayStatusDesc(payStatus.getDesc());
        }
        if (order.getPayChannel() != null) {
            PayChannel payChannel = PayChannel.of(order.getPayChannel());
            if (payChannel != null) {
                vo.setPayChannelDesc(payChannel.getDesc());
            }
        }
        return vo;
    }
}
