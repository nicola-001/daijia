package com.atguigu.daijia.customer.controller;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.login.Login;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.common.util.AuthContextHolder;
import com.atguigu.daijia.customer.client.CustomerInfoFeignClient;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "客户API接口管理")
@RestController
@RequestMapping("/customer")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerController {

    /*
    用户登录完整逻辑：
    * 用户请求 → 网关（StripPrefix + 路由到 web-customer）
    * → web-customer 处理 /order/...
    * → 内部通过 Feign 调用 @FeignClient(name = "service-customer")
    * → 请求到达 service-customer 的 Controller
    * → 返回结果逐层返回给用户
    * */

    @Autowired
    private CustomerService customerService;


    //登录
    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<String> login(@PathVariable String code) {
        return Result.ok(customerService.login(code));
    }


    //获取客户基本信息
    @Operation(summary = "获取客户登录信息")
    @Login
    @GetMapping("/getCustomerLoginInfo")
    public Result<CustomerLoginVo> getCustomerLoginInfo(@RequestHeader(value = "token") String token) {
        //1.从本地线程中获取用户id
        Long customerId = AuthContextHolder.getUserId();

        //2.调用service
        CustomerLoginVo customerLoginVo = customerService.getCustomerInfo(customerId);
        //4.返回用户信息
        return Result.ok(customerLoginVo);

    }
//    //获取客户基本信息
//    @Operation(summary = "获取客户登录信息")
//    @Login
//    @GetMapping("/getCustomerLoginInfo")
//    public Result<CustomerLoginVo> getCustomerLoginInfo(@RequestHeader(value = "token") String token) {
//        //1.从请求头中得到token字符串
//     CustomerLoginVo customerLoginVo =   customerService.getCustomerLoginInfo(token);
//        //4.返回用户信息
//        return Result.ok(customerLoginVo);
//
//    }
@Operation(summary = "更新用户微信手机号")
@Login
@PostMapping("/updateWxPhone")
public Result updateWxPhone(@RequestBody UpdateWxPhoneForm updateWxPhoneForm) {
    updateWxPhoneForm.setCustomerId(AuthContextHolder.getUserId());
    return Result.ok(customerService.updateWxPhoneNumber(updateWxPhoneForm));
}

}

