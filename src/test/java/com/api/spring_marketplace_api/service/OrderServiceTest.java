package com.api.spring_marketplace_api.service;

import com.api.spring_marketplace_api.enums.OrderStatus;
import com.api.spring_marketplace_api.model.dto.OrderResponseDto;
import com.api.spring_marketplace_api.model.entity.Cart;
import com.api.spring_marketplace_api.model.entity.CartItem;
import com.api.spring_marketplace_api.model.entity.Order;
import com.api.spring_marketplace_api.model.entity.Product;
import com.api.spring_marketplace_api.repository.CartRepository;
import com.api.spring_marketplace_api.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("Checkout Cart Test")
    class checkoutTest {

        @Test
        @DisplayName("Successfully cart checkout")
        void shouldCheckoutCartSuccessfully() {
            final UUID userId = UUID.fromString("66a509ba-c8a9-43fc-b2b5-45ba3284788c");


            Product product = new Product();
            product.setId(1L);
            product.setTitle("Test Product");
            product.setPrice(BigDecimal.valueOf(100.00));
            product.setQuantity(10);

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(2);

            Cart cart = new Cart();
            cart.setOwnerId(userId);

            cart.setItems(new ArrayList<>(List.of(cartItem)));

            when(cartRepository.findByOwnerIdWithItems(userId))
                    .thenReturn(Optional.of(cart));

            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            OrderResponseDto result = orderService.checkout(userId);

            assertNotNull(result);
            assertEquals(OrderStatus.CREATED, result.status());

            assertEquals(BigDecimal.valueOf(200.0), result.totalPrice());
            assertEquals(1, result.items().size());

            assertEquals(8, product.getQuantity());

            assertTrue(cart.getItems().isEmpty());

            verify(cartRepository, times(1)).save(cart);
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("Fail checkout when not enough stock")
        void shouldThrowException_WhenNotEnoughStock() {
            final UUID userId = UUID.randomUUID();

            Product product = new Product();
            product.setId(1L);
            product.setTitle("Iphone 15");
            product.setPrice(BigDecimal.valueOf(1000.00));
            product.setQuantity(2);

            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(5);

            Cart cart = new Cart();
            cart.setOwnerId(userId);
            cart.setItems(List.of(item));

            when(cartRepository.findByOwnerIdWithItems(userId))
                    .thenReturn(Optional.of(cart));

            assertThrows(IllegalArgumentException.class, () -> orderService.checkout(userId));

            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("Fail checkout when cart is empty")
        void shouldThrowException_WhenCartIsEmpty() {
            final UUID userId = UUID.randomUUID();

            Cart cart = new Cart();
            cart.setOwnerId(userId);
            cart.setItems(new ArrayList<>());

            when(cartRepository.findByOwnerIdWithItems(userId))
                    .thenReturn(Optional.of(cart));

            assertThrows(IllegalStateException.class, () -> orderService.checkout(userId));

            verify(orderRepository, never()).save(any(Order.class));
        }
    }
}