package com.shawn.medcom.payment;

import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {

    public Payment toPayment(PaymentRequest request) {
        if (request == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setId(request.id());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setAmount(request.amount());
        payment.setOrderId(request.orderId());

        return payment;
    }
}
