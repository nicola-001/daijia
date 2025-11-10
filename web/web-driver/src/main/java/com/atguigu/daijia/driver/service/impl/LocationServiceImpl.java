package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.LocationService;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.map.OrderServiceLocationForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationFeignClient locationFeignClient;
    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    //更新司机位置
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        //根据司机id获取司机个性化设置信息
        Long driverId = updateDriverLocationForm.getDriverId();
        Result<DriverSet> driverSetResult = driverInfoFeignClient.getDriverSet(driverId);
        DriverSet driverSet = driverSetResult.getData();

        //判断：如果司机开始接单，更新位置信息
        if (driverSet.getServiceStatus() == 1) {
            Result<Boolean> booleanResult = locationFeignClient.updateDriverLocation(updateDriverLocationForm);
            return booleanResult.getData();
        } else {
            //没有接单
            throw new GuiguException(ResultCodeEnum.NO_START_SERVICE);
        }
    }

    //删除司机位置
    @Override
    public Boolean removeDriverLocation(Long driverId) {
        Result<Boolean> booleanResult = locationFeignClient.removeDriverLocation(driverId);
        if (!booleanResult.getData()) {
            throw new GuiguException(ResultCodeEnum.FAIL);
        }
        return booleanResult.getData();
    }

    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        Boolean data = locationFeignClient.updateOrderLocationToCache(updateOrderLocationForm).getData();
        if (!data) {
            throw new GuiguException(ResultCodeEnum.FAIL);
        }
        return data;
    }

    //保存订单服务位置
    @Override
    public Boolean saveOrderServiceLocation(List<OrderServiceLocationForm> orderLocationServiceFormList) {
        Result<Boolean> booleanResult = locationFeignClient.saveOrderServiceLocation(orderLocationServiceFormList);
        if (booleanResult.getCode() != 200) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return true;
    }
}
