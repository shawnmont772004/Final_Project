package com.shawn.medcom.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        String id,
        @NotNull(message="Customer firstname is required")
        String firstname,
        @NotNull(message="Customer lastname is required")
        String lastname,
        @NotNull(message="Customer email is required")
        @Email(message="Customer email is not a valid email address")
        String email,
        Address address//v have addded validated annotation in Address so no need any valid annotation
         ) {


}
