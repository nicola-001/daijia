package com.atguigu.daijia.customer.client;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//Feign客户端要调用的服务注册名是service-customer
@FeignClient(value = "service-customer")
public interface CustomerInfoFeignClient {

    //远程调用：定义登录接口
    @GetMapping("/customer/info/login/{code}")
    public Result<Long> login(@PathVariable String code);

    //远程调用：定义获取客户信息接口
    @GetMapping("/customer/info/getCustomerInfo/{customerId}")
    public Result<CustomerLoginVo> getCustomerLoginInfo(@PathVariable Long customerId);

    //远程调用：定义更新微信手机号接口
    @PostMapping("/customer/info/updateWxPhoneNumber")
    public Result<Boolean> updateWxPhoneNumber(@RequestBody UpdateWxPhoneForm updateWxPhoneForm);


}