package com.varun.vcommercestore.Models;

import com.varun.vcommercestore.Enums.PaymentMethods;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.internal.bytebuddy.asm.Advice;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Orders")
public class Order {
    //order related
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderid;

    private double totalamount;

    @Enumerated(EnumType.STRING)
    private PaymentMethods PaymentMethod;

    //details fields
    private String OrderAddress;

    private String OrderName;

    private String orderPhoneNumber;

    private LocalDateTime orderdate;

    //mapping fields
    @ManyToOne
    @JoinColumn(name = "userid",nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItems> orderItemsList;

}
