package com.shawn.medcom.kafka;

import com.shawn.medcom.email.EmailService;
import com.shawn.medcom.kafka.order.OrderConfirmation;
import com.shawn.medcom.kafka.payment.PaymentConfirmation;
import com.shawn.medcom.notification.Notification;
import com.shawn.medcom.notification.NotificationRepository;
import com.shawn.medcom.notification.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;

import java.time.LocalDateTime;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationRepository repository;
    private final EmailService emailService;

    // Constructor for dependency injection
    public NotificationConsumer(NotificationRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(PaymentConfirmation paymentConfirmation) {
        log.info(String.format("Consuming the message from payment-topic Topic:: %s", paymentConfirmation));

        Notification notification = new Notification();
        notification.setType(NotificationType.PAYMENT_CONFIRMATION);
        notification.setNotificationDate(LocalDateTime.now());
        notification.setPaymentConfirmation(paymentConfirmation);

        // Send email
        String customerName = paymentConfirmation.customerFirstname() + " " + paymentConfirmation.customerLastname();
        try {
            emailService.sendPaymentSuccessEmail(
                    paymentConfirmation.customerEmail(),
                    customerName,
                    paymentConfirmation.amount(),
                    paymentConfirmation.orderReference()
            );
        } catch (MessagingException e) {
            log.error("Error sending email for payment confirmation", e);
        }
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotifications(OrderConfirmation orderConfirmation) {
        log.info(String.format("Consuming the message from order-topic Topic:: %s", orderConfirmation));

        Notification notification = new Notification();
        notification.setType(NotificationType.ORDER_CONFIRMATION);
        notification.setNotificationDate(LocalDateTime.now());
        notification.setOrderConfirmation(orderConfirmation);

        repository.save(notification);

        String customerName = orderConfirmation.customer().firstname() + " " + orderConfirmation.customer().lastname();
        try {
            emailService.sendOrderConfirmationEmail(
                    orderConfirmation.customer().email(),
                    customerName,
                    orderConfirmation.totalAmount(),
                    orderConfirmation.orderReference(),
                    orderConfirmation.products()
            );
        } catch (MessagingException e) {
            log.error("Error sending email for order confirmation", e);
        }
    }
}
