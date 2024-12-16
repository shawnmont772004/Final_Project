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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;
    private final OrderLineService orderLineService;

    // Constructor for dependency injection (replacing @RequiredArgsConstructor)
    public OrderService(
            OrderRepository repository,
            OrderMapper mapper,
            CustomerClient customerClient,
            ProductClient productClient,
            OrderProducer orderProducer,
            PaymentClient paymentClient,
            OrderLineService orderLineService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.customerClient = customerClient;
        this.productClient = productClient;
        this.orderProducer = orderProducer;
        this.paymentClient = paymentClient;
        this.orderLineService = orderLineService;
    }

    @Transactional
    public Integer createOrder(OrderRequest request) {

        // Check the customer via OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        // Purchase the products via product-ms
        var purchasedProducts = productClient.purchaseProducts(request.products());

        // Persist order
        var order = this.repository.save(mapper.toOrder(request));

        // Persist order lines
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

        // Start payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );

        paymentClient.requestOrderPayment(paymentRequest);

        // Send the order confirmation via notification-ms (Kafka)
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

    // Find all orders logic
    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    // Find by ID logic
    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
