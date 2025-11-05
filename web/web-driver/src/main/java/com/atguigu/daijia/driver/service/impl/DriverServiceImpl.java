package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.dispatch.client.NewOrderFeignClient;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.map.client.LocationFeignClient;
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
    @Autowired
    private LocationFeignClient locationFeignClient;
    @Autowired
    private NewOrderFeignClient newOrderFeignClient;

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

    //判断司机当日是否进行过人脸识别
    @Override
    public Result<Boolean> isFaceRecognition(Long driverId) {

        Result<Boolean> faceRecognition = driverInfoFeignClient.isFaceRecognition(driverId);
        if (faceRecognition.getCode() != 200) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return faceRecognition;
    }

    //人脸识别
    @Override
    public Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm) {
        return driverInfoFeignClient.verifyDriverFace(driverFaceModelForm).getData();
    }

    @Override
    public Boolean startService(Long driverId) {
        //1.判断是否完成认证
        DriverLoginVo driverLoginVo = driverInfoFeignClient.getDriverInfo(driverId).getData();
        if (driverLoginVo.getAuthStatus() != 2) {
            throw new GuiguException(ResultCodeEnum.AUTH_ERROR);
        }
        //2.判断是否完成人脸识别
        Boolean isFace = driverInfoFeignClient.isFaceRecognition(driverId).getData();
        if (!isFace) {
            throw new GuiguException(ResultCodeEnum.FACE_ERROR);
        }
        //3.更新订单状态 1 开始接单
        driverInfoFeignClient.updateServiceStatus(driverId, 1);
        //4.删除司机的位置信息
        locationFeignClient.removeDriverLocation(driverId);
        //5.清空司机临时队列数据
        newOrderFeignClient.clearNewOrderQueueData(driverId);
        return true;
    }

    //停止接单服务
    @Override
    public Boolean stopService(Long driverId) {
        //更新司机的接单状态 0
        driverInfoFeignClient.updateServiceStatus(driverId,0);

        //删除司机位置信息
        locationFeignClient.removeDriverLocation(driverId);

        //清空司机临时队列
        newOrderFeignClient.clearNewOrderQueueData(driverId);
        return true;
    }

}
