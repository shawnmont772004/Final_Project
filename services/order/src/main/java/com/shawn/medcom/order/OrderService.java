package com.shawn.medcom.order;

import com.shawn.medcom.customer.CustomerClient;
import com.shawn.medcom.exception.BusinessException;
import com.shawn.medcom.kafka.OrderConfirmation;
import com.shawn.medcom.orderline.OrderLineRequest;
import com.shawn.medcom.orderline.OrderLineService;
import com.shawn.medcom.payment.PaymentClient;
import com.shawn.medcom.payment.PaymentRequest;
import com.shawn.medcom.product.ProductClient;
import com.shawn.medcom.product.PurchaseRequest;
import com.shawn.medcom.kafka.OrderProducer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;
    private final OrderLineService orderLineService;

    @Transactional
    public Integer createOrder(OrderRequest request) {
        //check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        //purchase the products --> product-ms
        var purchasedProducts = productClient.purchaseProducts(request.products());

        //persist order
        var order = this.repository.save(mapper.toOrder(request));

        //persist order lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        //start payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );

        paymentClient.requestOrderPayment(paymentRequest);

        //send the order confirmation -->notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();


    }

    //to find all orders logic
    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    //find by id
    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
