package com.ecommerce.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderCreateRequest {
    @NotEmpty
    @Valid
    private List<OrderLineRequest> items;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String country;

    @Data
    public static class OrderLineRequest {
        @NotNull
        private Long productId;

        @NotNull
        @Positive
        private Integer quantity;
    }
}