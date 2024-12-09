package com.shawn.medcom.orderline;

import com.netflix.spectator.api.Registry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


import java.util.List;

public interface OrderLineRepository extends JpaRepository<OrderLine, Integer> {

    List<OrderLine> findAllByOrderId(Integer orderId);
}