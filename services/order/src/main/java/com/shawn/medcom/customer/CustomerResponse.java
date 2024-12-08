package com.shawn.medcom.customer;

public record CustomerResponse(
        String id,
        String firstname,
        String lastname,
        String email
) {

}
