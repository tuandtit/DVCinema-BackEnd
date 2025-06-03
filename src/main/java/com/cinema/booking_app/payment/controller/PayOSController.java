package com.cinema.booking_app.payment.controller;

import com.cinema.booking_app.payment.service.PayOSService;
import com.cinema.booking_app.payment.type.CreatePaymentLinkRequestBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PayOSController {

    private final PayOSService payOSService;
    private final vn.payos.PayOS payOS;

    @GetMapping("/test")
    public String test() {
        return "Test";
    }

    @PostMapping("/create")
    public Map<String, Object> createPayment(@RequestBody CreatePaymentLinkRequestBody requestBody) throws Exception {
        return payOSService.createPaymentLink(requestBody);
    }

    @PostMapping(path = "/payos_transfer_handler")
    public ObjectNode payosTransferHandler(@RequestBody ObjectNode body)
            throws JsonProcessingException, IllegalArgumentException {
        return payOSService.payosTransferHandler(body);
    }

    @PutMapping(path = "/{orderId}")
    public ObjectNode cancelOrder(@PathVariable("orderId") Long orderId) {
        return payOSService.cancelOrder(orderId);
    }

    @GetMapping(path = "/{orderId}")
    public ObjectNode getOrderById(@PathVariable("orderId") Long orderId) {
        return payOSService.getOrderById(orderId);
    }

    @PostMapping(path = "/confirm-webhook")
    public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
        return payOSService.confirmWebhook(requestBody);
    }
}