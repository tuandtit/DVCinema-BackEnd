package com.cinema.booking_app.payment.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class CreatePaymentLinkRequestBody {
    private String productName;
    private String description;
    private String returnUrl;
    private int price;
    private Long showtimeId;
    private Set<Long> seatShowtimeIds;
    private String cancelUrl;
}