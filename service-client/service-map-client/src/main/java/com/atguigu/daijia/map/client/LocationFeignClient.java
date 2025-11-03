package com.atguigu.daijia.map.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-map")
public interface LocationFeignClient {

    //远程调用：更新司机经纬度位置
    @PostMapping("/map/location/updateDriverLocation")
    public Result<Boolean> updateDriverLocation(@RequestBody
                                                UpdateDriverLocationForm updateDriverLocationForm);

    //远程调用：删除司机经纬度位置
    @PostMapping("/map/location/removeDriverLocation")
    public Result<Boolean> removeDriverLocation(@PathVariable Long driverId);
}