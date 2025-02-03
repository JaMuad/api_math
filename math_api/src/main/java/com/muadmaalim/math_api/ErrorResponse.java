package com.muadmaalim.math_api;

import lombok.Getter;
import lombok.Setter;

// Error Response DTO (for 400 Bad Request)
@Getter
@Setter
public class ErrorResponse {
    private String number;
    private Boolean error = true;
}
