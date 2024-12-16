package com.shawn.medcom.exception;

public class BusinessException extends RuntimeException {

    private final String msg;

    // Constructor to initialize the exception with a custom message
    public BusinessException(String msg) {
        super(msg); // Calls the constructor of the parent class RuntimeException
        this.msg = msg; // Initializes the custom `msg` field
    }

    // Getter for the `msg` field
    public String getMsg() {
        return msg;
    }

    // Overriding equals method for object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusinessException that = (BusinessException) o;

        return msg != null ? msg.equals(that.msg) : that.msg == null;
    }

    // Overriding hashCode method for hashing functionality
    @Override
    public int hashCode() {
        return msg != null ? msg.hashCode() : 0;
    }

    // Overriding toString method to return the string representation of the exception
    @Override
    public String toString() {
        return "BusinessException{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
