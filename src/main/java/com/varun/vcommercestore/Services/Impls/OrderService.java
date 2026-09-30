package com.varun.vcommercestore.Services.Impls;

import com.varun.vcommercestore.Exceptions.ResourceNotFoundException;
import com.varun.vcommercestore.Models.*;
import com.varun.vcommercestore.Repositories.*;
import com.varun.vcommercestore.Services.OrderServices;
import com.varun.vcommercestore.dtos.Requestdtos.OrderRequestDto;
import com.varun.vcommercestore.dtos.Responcedtos.OrderItemsResponseDto;
import com.varun.vcommercestore.dtos.Responcedtos.OrderResponseDto;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements OrderServices {

    @Autowired
    ModelMapper mapper;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartRepository cartRepository;




    @Override
    @Transactional
    public OrderResponseDto CreateOrder(String userid, OrderRequestDto dto) {

        User user = userRepository.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException("user not found!"));

        Cart cart = user.getCart();

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        List<OrderItems> orderItemsList = new ArrayList<>();
        double totalamount = 0;

        for (CartItems cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            Reservation reservation = reservationRepository.findByCartAndProduct(cart, product);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation expired for " + product.getProductname() + ", please checkout again");
            }

            // finalize the sale
            product.setQuantity(product.getQuantity() - quantity);
            product.setReservedQuantity(product.getReservedQuantity() - quantity);
            productRepository.save(product);

            reservationRepository.delete(reservation);

            double itemPrice = product.getDiscountPrice().doubleValue();
            totalamount += itemPrice * quantity;

            OrderItems orderItem = OrderItems.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(itemPrice)
                    .build();

            orderItemsList.add(orderItem);
        }

        Order order = Order.builder()
                .totalamount(totalamount)
                .PaymentMethod(dto.getPaymentMethod())
                .OrderAddress(dto.getOrderAddress())
                .OrderName(dto.getOrderName())
                .orderPhoneNumber(dto.getOrderPhoneNumber())
                .user(user)
                .orderdate(LocalDateTime.now())
                .orderItemsList(orderItemsList)
                .build();

        for (OrderItems item : orderItemsList) {
            item.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);

        //clear the cart
        cart.getCartItems().clear();
        cart.setTotalItems(0);
        cart.setTotalprice(0.0);
        cartRepository.save(cart);

        return entityToOrderDto(savedOrder);
    }



    @Override
    @Transactional
    public List<OrderResponseDto> getOrdersByUser(String userid) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException("user not found!"));

        List<Order> orders = orderRepository.findByUser(user);

        List<OrderResponseDto> response = new ArrayList<>();
        for (Order order : orders) {
            response.add(entityToOrderDto(order));
        }
        return response;
    }




    private OrderResponseDto entityToOrderDto(Order savedOrder) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setPaymentMethod(savedOrder.getPaymentMethod());
        dto.setOrderPhoneNumber(savedOrder.getOrderPhoneNumber());
        dto.setOrderdate(savedOrder.getOrderdate());
        dto.setOrderAddress(savedOrder.getOrderAddress());
        dto.setOrderName(savedOrder.getOrderName());
        dto.setOrderid(savedOrder.getOrderid());
        dto.setTotalamount(savedOrder.getTotalamount());
        List<OrderItemsResponseDto> items = new ArrayList<>();
        for (OrderItems item : savedOrder.getOrderItemsList()) {
            Product product = item.getProduct();

            OrderItemsResponseDto itemDto = new OrderItemsResponseDto();
            itemDto.setProductid(product.getProductid());
            itemDto.setProductname(product.getProductname());
            itemDto.setProductImage(product.getProductImage());
            itemDto.setPrice(item.getPrice());
            itemDto.setQuantity(item.getQuantity());

            items.add(itemDto);
        }
        dto.setOrderItemsList(items);


        return dto;
    }


}
