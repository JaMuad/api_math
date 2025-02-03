package com.muadmaalim.math_api;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class NumberService {

    public boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    public boolean isPerfect(int number) {
        if (number <= 1) return false;
        int sum = 1;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                sum += i;
                int otherDivisor = number / i;
                if (otherDivisor != i) sum += otherDivisor;
            }
        }
        return sum == number;
    }

    public boolean isArmstrong(int number) {
        int original = number, digits = String.valueOf(number).length(), sum = 0;
        while (number > 0) {
            int digit = number % 10;
            sum += Math.pow(digit, digits);
            number /= 10;
        }
        return sum == original;
    }

    public String getFunFact(int number) {
        try {
            String url = "http://numbersapi.com/" + number + "/math?json";
            return new RestTemplate().getForObject(url, JsonNode.class).get("text").asText();
        } catch (Exception e) {
            return "No fun fact available.";
        }
    }

    public String[] getProperties(int number) {
        List<String> properties = new ArrayList<>();
        if (isArmstrong(number)) {
            properties.add("armstrong");
        }
        properties.add(getParity(number));
        return properties.toArray(new String[0]);
    }

    private String getParity(int number) {
        return (number % 2 == 0) ? "even" : "odd";
    }
}