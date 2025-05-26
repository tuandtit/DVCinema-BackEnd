package com.cinema.booking_app.payment.service;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.service.BookingService;
import com.cinema.booking_app.config.PayOSConfig;
import com.cinema.booking_app.payment.type.CreatePaymentLinkRequestBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayOSService {

    private final PayOSConfig payOSConfig;
    private final PayOS payOS;
    private final BookingService bookingService;

    public Map<String, Object> createPaymentLink(CreatePaymentLinkRequestBody requestBody) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();
        try {
            final String returnUrl = payOSConfig.returnUrl;
            final String cancelUrl = payOSConfig.cancelUrl;
            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .showtimeId(requestBody.getShowtimeId())
                    .seatShowtimeIds(requestBody.getSeatShowtimeIds())
                    .totalPrice(BigDecimal.valueOf(requestBody.getPrice()))
                    .build();

            BookingResponseDto booking = bookingService.createBooking(bookingRequestDto);

            ItemData item = ItemData.builder()
                    .name(requestBody.getProductName())
                    .price(requestBody.getPrice())
                    .quantity(requestBody.getSeatShowtimeIds().size())
                    .build();
            final Long expiredAt = System.currentTimeMillis() / 1000 + 600;

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(booking.getBookingCode())
                    .description(requestBody.getDescription())
                    .amount(requestBody.getPrice())
                    .item(item)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .expiredAt(expiredAt)
                    .build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            response.put("error", 0);
            response.put("message", "success");
            response.put("data", objectMapper.valueToTree(data));
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", "fail");
            response.put("data", null);
            return response;

        }
    }

    public ObjectNode payosTransferHandler(ObjectNode body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);

        try {
            // Init Response
            response.put("error", 0);
            response.put("message", "Webhook delivered");
            response.set("data", null);

            WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);

            System.out.println(data.toString());
            Long orderCode = data.getOrderCode();
            String code = data.getCode();
            String desc = data.getDesc();
            if (code.equals("00") || desc.contains("Success")) {
                bookingService.confirmPayment(orderCode);
            } else {
                bookingService.deleteBooking(orderCode);
                response.put("error", 1);
                response.put("message", "Payment failed");
                response.set("data", null);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    public ObjectNode cancelOrder(Long orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            PaymentLinkData order = payOS.cancelPaymentLink(orderId, null);
            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    public ObjectNode getOrderById(Long orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            PaymentLinkData order = payOS.getPaymentLinkInformation(orderId);

            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    public ObjectNode confirmWebhook(Map<String, String> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            String str = payOS.confirmWebhook(requestBody.get("webhookUrl"));
            response.set("data", objectMapper.valueToTree(str));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }
}