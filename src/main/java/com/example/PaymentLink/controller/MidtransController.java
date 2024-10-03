package com.example.PaymentLink.controller;

import com.example.PaymentLink.service.MidtransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/midtrans")
public class MidtransController {

    @Autowired
    private MidtransService midtransService;

    @PostMapping("/create-payment-link")
    public ResponseEntity<String> createPaymentLink(@RequestBody Map<String, Object> paymentLinkRequest) {
        try {
            String response = midtransService.createPaymentLink(paymentLinkRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the exception and return a bad request response
            return ResponseEntity.badRequest().body("Error creating payment link: " + e.getMessage());
        }
    }
}
