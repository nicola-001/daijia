package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.dispatch.client.NewOrderFeignClient;
import com.atguigu.daijia.driver.service.OrderService;
import com.atguigu.daijia.map.client.MapFeignClient;
import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.form.map.CalculateDrivingLineForm;
import com.atguigu.daijia.model.vo.map.DrivingLineVo;
import com.atguigu.daijia.model.vo.order.CurrentOrderInfoVo;
import com.atguigu.daijia.model.vo.order.NewOrderDataVo;
import com.atguigu.daijia.model.vo.order.OrderInfoVo;
import com.atguigu.daijia.order.client.OrderInfoFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderInfoFeignClient orderInfoFeignClient;
    @Autowired
    private NewOrderFeignClient newOrderFeignClient;
    @Autowired
    private MapFeignClient mapFeignClient;

    @Override
    public Integer getOrderStatus(Long orderId) {
        Result<Integer> orderStatus = orderInfoFeignClient.getOrderStatus(orderId);
        if (orderStatus.getData() != null) {
            return orderStatus.getData();
        }
        return orderStatus.getData();
    }

    @Override
    public List<NewOrderDataVo> findNewOrderQueueData(Long driverId) {
        return newOrderFeignClient.findNewOrderQueueData(driverId).getData();
    }

    //司机抢单功能
    @Override
    public Boolean robNewOrder(Long driverId, Long orderId) {
        Result<Boolean> order = orderInfoFeignClient.robNewOrder(driverId, orderId);
        return order.getData();
    }

    //司机端查找当前订单功能
    @Override
    public CurrentOrderInfoVo searchDriverCurrentOrder(Long driverId) {
        return orderInfoFeignClient.searchDriverCurrentOrder(driverId).getData();
    }

    @Override
    public OrderInfoVo getOrderInfo(Long orderId, Long driverId) {
        //远程调用
        OrderInfo orderInfo = orderInfoFeignClient.getOrderInfo(orderId).getData();
        //判断
        if (orderInfo.getDriverId() != driverId) {
            throw new GuiguException(ResultCodeEnum.ILLEGAL_REQUEST);
        }
        //封装对象返回
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        orderInfoVo.setOrderId(orderId);
        BeanUtils.copyProperties(orderInfo, orderInfoVo);
        return orderInfoVo;
    }

    // 计算最佳路线
    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {
        Result<DrivingLineVo> drivingLineVoResult = mapFeignClient.calculateDrivingLine(calculateDrivingLineForm);
        if (drivingLineVoResult.getCode() != 200) {
            throw new GuiguException(drivingLineVoResult.getCode(), drivingLineVoResult.getMessage());
        }
        return drivingLineVoResult.getData();
    }

    //司机到达开始位置，更新订单数据
    @Override
    public Boolean driverArriveStartLocation(Long orderId, Long driverId) {
        Result<Boolean> booleanResult = orderInfoFeignClient.driverArriveStartLocation(orderId, driverId);
        if (booleanResult.getCode() != 200){
            throw new GuiguException(booleanResult.getCode(), booleanResult.getMessage());
        }
        return booleanResult.getData();
    }
}
