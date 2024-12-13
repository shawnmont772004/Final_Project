package com.shawn.medcom.email;

public enum EmailTemplates {

    PAYMENT_CONFIRMATION("payment-confirmation.html", "Payment successfully processed"),
    ORDER_CONFIRMATION("order-confirmation.html", "Order confirmation");

    private final String template;
    private final String subject;

    // Constructor to replace Lombok's @Getter
    EmailTemplates(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }

    // Getter methods to replace Lombok's @Getter
    public String getTemplate() {
        return template;
    }

    public String getSubject() {
        return subject;
    }
}

