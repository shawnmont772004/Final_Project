package com.shawn.medcom.customer;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document //mngodb specification
@Validated
public class Address {
    @Id
    private String street;
    private String houseNumber;
    private String zipCode;
}
