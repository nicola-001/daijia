package com.atguigu.daijia.map.service.impl;


import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.map.service.LocationService;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Autowired
    private RedisTemplate redisTemplate;

    //司机开始接单：更新司机位置信息
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        //把司机位置信息添加redis里面geo
        Point point = new Point(updateDriverLocationForm.getDriverId().doubleValue(), updateDriverLocationForm.getLongitude().doubleValue());
        //添加到redis里面
        redisTemplate.opsForGeo().add(RedisConstant.DRIVER_GEO_LOCATION,
                point,
                updateDriverLocationForm.getDriverId().toString());
        return true;
    }

    //司机停止接单：删除司机位置信息
    @Override
    public Boolean removeDriverLocation(Long driverId) {
        Long remove = redisTemplate.opsForGeo().remove(RedisConstant.DRIVER_GEO_LOCATION, driverId.toString());
        if (remove == 0) {
            return false;
        }
        return true;
    }


}
