package com.muadmaalim.math_api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    private Integer number;
    private Boolean is_prime;
    private Boolean is_perfect;
    private String[] properties;
    private Integer digit_sum;
    private String fun_fact;
}

