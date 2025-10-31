package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.model.form.customer.UpdateCustomerInfoForm;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverServiceImpl implements DriverService {
    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String login(String code) {
        //远程调用获取司机id
        Result<Long> longResult = driverInfoFeignClient.login(code);
        Long driverId = longResult.getData();
        //生成token字符串
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        //放到redis中并设置过期时间
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX + token,
                driverId.toString(),
                RedisConstant.USER_LOGIN_KEY_TIMEOUT,
                TimeUnit.SECONDS);
        return token;
    }

    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        Result<DriverAuthInfoVo> driverAuthInfo = driverInfoFeignClient.getDriverAuthInfo(driverId);
        log.info("获取司机认证信息service222222222：{}", driverAuthInfo);
        if (driverAuthInfo.getCode() != 200 || driverAuthInfo.getData() == null) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        System.out.println("司机认证信息为：" + driverAuthInfo);
        return driverAuthInfo.getData();

    }

    //更新司机认证信息
    @Override
    public Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        Result<Boolean> booleanResult = driverInfoFeignClient.UpdateDriverAuthInfo(updateDriverAuthInfoForm);
        return booleanResult.getData();
    }

    //创建司机人脸模型
    @Override
    public Result<Boolean> createDriverFaceModel(DriverFaceModelForm driverFaceModelForm) {
        Result<Boolean> booleanResult = driverInfoFeignClient.createDriverFaceModel(driverFaceModelForm);
        if (booleanResult.getCode() != 200) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return Result.ok(booleanResult.getData());
    }

}
