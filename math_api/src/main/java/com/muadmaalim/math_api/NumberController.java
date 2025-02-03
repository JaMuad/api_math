package com.muadmaalim.math_api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // This maps the base path for all endpoints in this controller
public class NumberController {

    private final NumberService numberService;

    public NumberController(NumberService numberService) {
        this.numberService = numberService;
    }

    @GetMapping("/classify-number") // This maps the specific endpoint
    public ResponseEntity<?> classifyNumber(@RequestParam String number) {
        try {
            int num = Integer.parseInt(number);
            ApiResponse response = new ApiResponse();
            response.setNumber(num);
            response.setIs_prime(numberService.isPrime(num));
            response.setIs_perfect(numberService.isPerfect(num));
            response.setProperties(numberService.getProperties(num));
            response.setDigit_sum(String.valueOf(num).chars().map(Character::getNumericValue).sum());
            response.setFun_fact(numberService.getFunFact(num));
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            ErrorResponse error = new ErrorResponse();
            error.setNumber(number);
            return ResponseEntity.badRequest().body(error);
        }
    }
}