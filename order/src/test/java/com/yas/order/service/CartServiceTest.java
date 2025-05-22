package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderVm;
import java.math.BigDecimal;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class CartServiceTest {

    private RestClient restClient;

    private ServiceUrlConfig serviceUrlConfig;

    private CartService cartService;

    private RestClient.ResponseSpec responseSpec;

    private static final String CART_URL = "http://api.yas.local/cart";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        cartService = new CartService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        setUpSecurityContext("test");
        when(serviceUrlConfig.cart()).thenReturn(CART_URL);
    }

    @Test
    void testDeleteCartItems_ifNormalCase_shouldNoException() {

        OrderVm orderVm = getOrderVm();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        assertDoesNotThrow(() -> cartService.deleteCartItems(orderVm));
    }

    private static @NotNull OrderVm getOrderVm() {
        Set<OrderItemVm> items = getOrderItemVms();

        return new OrderVm(
            1L,
            "customer@example.com",
            null,
            null,
            "Please deliver between 9 AM and 5 PM",
            5.0f,
            10.0f,
            3,
            new BigDecimal("89.97"),
            new BigDecimal("5.00"),
            "COUPON2024",
            OrderStatus.PENDING,
            DeliveryMethod.GRAB_EXPRESS,
            DeliveryStatus.CANCELLED,
            PaymentStatus.PENDING,
            items,
            UUID.randomUUID().toString()
        );
    }

    private static @NotNull Set<OrderItemVm> getOrderItemVms() {
        OrderItemVm item1 = new OrderItemVm(
            1L,
            101L,
            "Product A",
            2,
            new BigDecimal("29.99"),
            "Note for Product A",
            new BigDecimal("5.00"),
            new BigDecimal("2.00"),
            new BigDecimal("6.67"),
            1001L
        );

        OrderItemVm item2 = new OrderItemVm(
            2L,
            102L,
            "Product B",
            1,
            new BigDecimal("49.99"),
            "Note for Product B",
            new BigDecimal("10.00"),
            new BigDecimal("5.00"),
            new BigDecimal("10.00"),
            1001L
        );
        Set<OrderItemVm> items = new HashSet<>();
        items.add(item1);
        items.add(item2);
        return items;
    }
}
