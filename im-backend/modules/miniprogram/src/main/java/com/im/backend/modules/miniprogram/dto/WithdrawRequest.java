package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提现请求
 */
@Data
public class WithdrawRequest {

    @NotNull(message = "提现金额不能为空")
    @Min(value = 100, message = "最低提现金额为100元")
    private BigDecimal amount;

    /**
     * 提现方式：1-支付宝 2-微信 3-银行卡
     */
    @NotNull(message = "提现方式不能为空")
    private Integer withdrawType;

    /**
     * 账户信息
     */
    private String accountInfo;
}
