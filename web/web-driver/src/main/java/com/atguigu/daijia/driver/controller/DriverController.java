package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.login.Login;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.model.form.customer.UpdateCustomerInfoForm;
import com.atguigu.daijia.model.form.driver.DriverFaceModelForm;
import com.atguigu.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.atguigu.daijia.model.vo.driver.DriverAuthInfoVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "司机API接口管理")
@RestController
@RequestMapping(value = "/driver")
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverController {
    @Autowired
    private DriverService driverService;
    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<String> login(@PathVariable String code) {
        return Result.ok(driverService.login(code));
    }
    @Operation(summary = "获取司机登录信息")
    @Login
    @GetMapping("/getDriverLoginInfo")
    public Result<DriverLoginVo> getDriverLoginInfo() {
        //1 获取用户id
        Long driverId = AuthContextHolder.getUserId();
        log.info("获取司机登录信息controller：{}", driverId);
        //2 远程调用获取司机信息
        Result<DriverLoginVo> loginVoResult = driverInfoFeignClient.getDriverInfo(driverId);
        DriverLoginVo driverLoginVo = loginVoResult.getData();
        return Result.ok(driverLoginVo);
    }
    @Operation(summary = "获取司机认证信息")
    @Login
    @GetMapping("/getDriverAuthInfo")
    public Result<DriverAuthInfoVo> getDriverAuthInfo() {
        //获取用户id，当前是司机id
        Long driverId = AuthContextHolder.getUserId();
        DriverAuthInfoVo driverAuthInfo = driverService.getDriverAuthInfo(driverId);

        return Result.ok(driverAuthInfo);
    }

    @Operation(summary = "更新司机认证信息")
    @Login
    @PostMapping("/updateDriverAuthInfo")
    public Result<Boolean> updateDriverAuthInfo(@RequestBody UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        //获取用户id，当前是司机id
        Long driverId = AuthContextHolder.getUserId();
        updateDriverAuthInfoForm.setDriverId(driverId);
        Boolean isSuccess = driverService.updateDriverAuthInfo(updateDriverAuthInfoForm);
        return Result.ok(isSuccess);
    }
    @Operation(summary = "创建司机人脸模型")
    @Login
    @PostMapping("/creatDriverFaceModel")
    public Result<Boolean> createDriverFaceModel(@RequestBody DriverFaceModelForm driverFaceModelForm){
        Long driverId = AuthContextHolder.getUserId();
        driverFaceModelForm.setDriverId(driverId);
        Result<Boolean> driverFaceModel = driverService.createDriverFaceModel(driverFaceModelForm);
        return Result.ok(driverFaceModel.getData());
    }
}

