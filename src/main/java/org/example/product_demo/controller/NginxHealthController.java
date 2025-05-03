package org.example.product_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NginxHealthController {

    @GetMapping("/nginx-health")
    public String health() {
        return "OK";
    }
}