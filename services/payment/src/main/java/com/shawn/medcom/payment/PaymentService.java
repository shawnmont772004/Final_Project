package com.shawn.medcom.payment;

import com.shawn.medcom.notification.NotificationProducer;
import com.shawn.medcom.notification.PaymentNotificationRequest;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final NotificationProducer notificationProducer;

    // Constructor for dependency injection
    public PaymentService(PaymentRepository repository, PaymentMapper mapper, NotificationProducer notificationProducer) {
        this.repository = repository;
        this.mapper = mapper;
        this.notificationProducer = notificationProducer;
    }

    public Integer createPayment(PaymentRequest request) {
        // Convert request to Payment entity and save it
        Payment payment = this.repository.save(this.mapper.toPayment(request));

        // Create and send payment notification
        this.notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        request.orderReference(),
                        request.amount(),
                        request.paymentMethod(),
                        request.customer().firstname(),
                        request.customer().lastname(),
                        request.customer().email()
                )
        );

        // Return the ID of the saved payment
        return payment.getId();
    }
}
