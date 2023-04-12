package com.elvira.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
