package com.shawn.medcom.kafka;


import com.shawn.medcom.customer.CustomerResponse;
import com.shawn.medcom.order.PaymentMethod;
import com.shawn.medcom.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
