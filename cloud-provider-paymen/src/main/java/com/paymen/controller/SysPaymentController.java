package com.paymen.controller;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-06 15:25
 * @Version 1.0
 **/

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.core.resp.ResponseResult;
import com.paymen.entry.SysPaymentType;
import com.paymen.service.ISysPaymentService;
import com.paymen.sm234.common.base.ResponseBasic;
import com.paymen.sm234.common.constants.SecretConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * @author waves
 * @date 2024/4/25 9:44
 */
@RestController
@RequestMapping(value = {"/payment", SecretConstant.PREFIX + "/payment"})
public class SysPaymentController {

    @Autowired
    private ISysPaymentService paymentService;

    /**
     * 分页查询PaymentType，条件 payId ，payName
     *
     * @return
     */
//    @GetMapping("/listPage/{pageNum}/{pageSize}")
//    public IPage<SysPaymentType> list(@PathVariable("pageNum") Integer pageNum
//            , @PathVariable("pageSize") Integer pageSize
//            , SysPaymentType paymentType) {
//
//        QueryWrapper<SysPaymentType> queryWrapper = new QueryWrapper<>();
//        //判断支付类型名称 不为空，则进行条件查询
//        queryWrapper.lambda()
//                .like(Objects.nonNull(paymentType.getPayName()), SysPaymentType::getPayName, paymentType.getPayName())
//                .eq(Objects.nonNull(paymentType.getPayId()), SysPaymentType::getPayId, paymentType.getPayId());
//        return paymentService.page(new Page<>(pageNum, pageSize), queryWrapper);
//    }

    /**
     * 分页查询PaymentType，条件 payId ，payName
     *
     * @return
     */
    @RequestMapping("/listPage")
    public ResponseBasic<IPage<SysPaymentType>> list() {
        QueryWrapper<SysPaymentType> queryWrapper = new QueryWrapper<>();
        //判断支付类型名称 不为空，则进行条件查询
        Page<SysPaymentType> page = paymentService.page(new Page<>(1, 10), queryWrapper);
        ResponseBasic responseBasic = new ResponseBasic();
        responseBasic.setCode(200);
        responseBasic.setData(page);
        responseBasic.setMsg("列表查询成功");
        return responseBasic;
    }

    /**
     * 根据支付Id查询支付类型
     *
     * @param payId
     * @return
     */
    @GetMapping("/findById/{payId}")
    public SysPaymentType findById(@PathVariable("payId") Long payId) {
        return paymentService.getById(payId);
    }

    @PostMapping("/findById")
    public SysPaymentType findById(@RequestBody Map<String,String> map) {
        return paymentService.getById(map.get("id"));
    }

    /**
     * 根据Id进行删除
     *
     * @param payId
     * @return
     */
    @GetMapping("/removeById/{payId}")
    public ResponseResult removeById(@PathVariable("payId") Long payId) {
        return ResponseResult.success(paymentService.removeById(payId));
    }
}
