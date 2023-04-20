package com.elacode.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age) {

}
