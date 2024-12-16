package com.shawn.medcom.orderline;

import com.shawn.medcom.order.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderLineMapper {

    public OrderLine toOrderLine(OrderLineRequest request) {
        if (request == null) {
            return null;
        }
        Order order = new Order();
        order.setId(request.orderId());

        OrderLine orderLine = new OrderLine();
        orderLine.setId(request.orderId());
        orderLine.setProductId(request.productId());
        orderLine.setOrder(order);
        orderLine.setQuantity(request.quantity());
        return orderLine;
    }

    public OrderLineResponse toOrderLineResponse(OrderLine orderLine) {
        if (orderLine == null) {
            return null;
        }
        return new OrderLineResponse(
                orderLine.getId(),
                orderLine.getQuantity()
        );
    }
}
