package com.cccz.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cccz.common.vo.Result;
import com.cccz.order.service.OrderService;
import com.cccz.order.vo.OrderCancelReq;
import com.cccz.order.vo.OrderCreateReq;
import com.cccz.order.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 订单 Controller
 */
@RestController
@RequestMapping("/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<OrderVO> create(@Valid @RequestBody OrderCreateReq req) {
        log.info("创建订单请求, userId={}", req.getUserId());
        OrderVO vo = orderService.createOrder(req);
        return Result.success(vo);
    }

    /**
     * 根据ID查询订单
     */
    @GetMapping("/{orderId}")
    public Result<OrderVO> getById(@PathVariable Long orderId) {
        log.info("查询订单, orderId={}", orderId);
        OrderVO vo = orderService.getOrderById(orderId);
        if (vo == null) {
            return Result.fail("订单不存在");
        }
        return Result.success(vo);
    }

    /**
     * 根据订单号查询
     */
    @GetMapping("/no/{orderNo}")
    public Result<OrderVO> getByNo(@PathVariable String orderNo) {
        log.info("查询订单, orderNo={}", orderNo);
        OrderVO vo = orderService.getOrderByNo(orderNo);
        if (vo == null) {
            return Result.fail("订单不存在");
        }
        return Result.success(vo);
    }

    /**
     * 分页查询用户订单
     */
    @GetMapping("/page/user/{userId}")
    public Result<IPage<OrderVO>> pageUserOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("分页查询用户订单, userId={}, orderStatus={}, pageNum={}, pageSize={}",
                userId, orderStatus, pageNum, pageSize);
        IPage<OrderVO> page = orderService.pageUserOrders(userId, orderStatus, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    public Result<Void> cancel(@Valid @RequestBody OrderCancelReq req) {
        log.info("取消订单请求, orderId={}", req.getOrderId());
        orderService.cancelOrder(req);
        return Result.success();
    }

    /**
     * 支付订单
     */
    @PostMapping("/{orderId}/pay")
    public Result<Void> pay(@PathVariable Long orderId,
                             @RequestParam String payTransactionNo,
                             @RequestParam Integer payChannel) {
        log.info("支付订单, orderId={}, payChannel={}", orderId, payChannel);
        orderService.payOrder(orderId, payTransactionNo, payChannel);
        return Result.success();
    }

    /**
     * 发货
     */
    @PostMapping("/{orderId}/ship")
    public Result<Void> ship(@PathVariable Long orderId,
                              @RequestParam String deliveryCompany,
                              @RequestParam String deliveryNo) {
        log.info("发货, orderId={}, deliveryCompany={}", orderId, deliveryCompany);
        orderService.shipOrder(orderId, deliveryCompany, deliveryNo);
        return Result.success();
    }

    /**
     * 完成订单
     */
    @PostMapping("/{orderId}/finish")
    public Result<Void> finish(@PathVariable Long orderId) {
        log.info("完成订单, orderId={}", orderId);
        orderService.finishOrder(orderId);
        return Result.success();
    }
}
