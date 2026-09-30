package com.varun.vcommercestore.dtos.Requestdtos;

import com.varun.vcommercestore.Enums.PaymentMethods;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    private String orderAddress;

    private String orderName;

    private String orderPhoneNumber;

    private PaymentMethods paymentMethod;
}
