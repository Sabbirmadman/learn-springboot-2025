package com.lelarn.dreamshops.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotNull
    private Long productId;

    @Min(1)
    private int quantity;
}
