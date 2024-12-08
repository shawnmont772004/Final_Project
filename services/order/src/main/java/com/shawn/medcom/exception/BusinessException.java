package com.shawn.medcom.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private final String msg;

    public BusinessException(String msg) {
        super(msg); // Sets the `message` field in the parent class
        this.msg = msg; // Initializes your custom field
    }
}
