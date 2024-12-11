package com.shawn.medcom.payment;


import com.shawn.medcom.customer.CustomerResponse;
import com.shawn.medcom.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
