package com.paymen.entry;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-06 15:22
 * @Version 1.0
 **/

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 支付类型实体 如果进行网络通信 一定要实现序列化
 * @author waves
 * @date 2024/4/25 9:44
 */
@Data //: 该注解会自动生成类的 getter、setter、equals、hashCode 和 toString 方法，从而减少样板代码。
@NoArgsConstructor //: 自动生成一个无参构造方法，用于创建对象实例时不需要传入任何参数。
@AllArgsConstructor //: 自动生成一个包含所有参数的构造方法，用于创建对象实例时需要传入所有字段的值。
@Accessors(chain = true) //: 启用方法链（method chaining），允许在对象实例上连续调用多个 setter 方法，可以使代码更加简洁。
@TableName("sys_payment_type") //:对应数据库表的名称
public class SysPaymentType implements Serializable {

    /**
     * 支付Id
     * TableId 加在表主键上面 value 对应 字段名 ，type 为数据库ID自增
     */
    @TableId(value = "pay_id", type = IdType.AUTO)
    private Long payId;

    /**
     * 支付类型名称
     * TableField value 对应 字段名
     */
    @TableField(value = "pay_name")
    private String payName;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}
