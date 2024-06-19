package com.paymen.service.impl;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-06 15:24
 * @Version 1.0
 **/

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paymen.entry.SysPaymentType;
import com.paymen.mapper.SysPaymentMapper;
import com.paymen.service.ISysPaymentService;
import org.springframework.stereotype.Service;

/**
 * @author waves
 * @date 2024/4/25 9:44
 */
@Service
public class SysPaymentServiceImpl extends ServiceImpl<SysPaymentMapper, SysPaymentType> implements ISysPaymentService {
}
