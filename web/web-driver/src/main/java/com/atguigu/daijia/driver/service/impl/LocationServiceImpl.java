package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.service.LocationService;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationFeignClient locationFeignClient;

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        Result<Boolean> booleanResult = locationFeignClient.updateDriverLocation(updateDriverLocationForm);
        if (!booleanResult.getData()) {
            throw new GuiguException(ResultCodeEnum.FAIL);
        }
        return booleanResult.getData();

    }

    @Override
    public Boolean removeDriverLocation(Long driverId) {
        Result<Boolean> booleanResult = locationFeignClient.removeDriverLocation(driverId);
        if (!booleanResult.getData()) {
            throw new GuiguException(ResultCodeEnum.FAIL);
        }
        return booleanResult.getData();
    }
}
