// package com.demo.ecommerce_backend.order;

// import com.demo.ecommerce_backend.orderItem.OrderItem;
// import com.demo.ecommerce_backend.orderItem.OrderItemRepository;
// import com.demo.ecommerce_backend.product.Product;
// import com.demo.ecommerce_backend.product.ProductSource;
// import com.demo.ecommerce_backend.smileone.SmileOneOrderRequest;
// import com.demo.ecommerce_backend.smileone.SmileOneTpClient;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.*;
// import org.springframework.boot.test.context.SpringBootTest;
// import static org.junit.jupiter.api.Assertions.*;
// import java.math.BigDecimal;
// import java.util.List;

// import static org.mockito.Mockito.*;

// public class OrderServiceTest {

//     @Mock
//     private OrderItemRepository orderItemRepository;

//     @Mock
//     private OrderRepository orderRepository;

//     @Mock
//     private SmileOneTpClient smileOneTpClient;

//     @InjectMocks
//     private FulfillmentService fulfillmentService;

//     @Captor
//     private ArgumentCaptor<SmileOneOrderRequest> smileOneRequestCaptor;

//     @BeforeEach
//     public void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     void testProcessOrder_SmileOneRequest() {
//         // Mock Order
//         Order order = Order.builder()
//                 .id(1)
//                 .fulfilled(false)
//                 .build();

//         // Product (SmileOne)
//         Product smileProduct = Product.builder()
//                 .id(101)
//                 .name("Mobile Legends Diamond")
//                 .externalProductId("22590")
//                 .source(ProductSource.SMILEONE)
//                 .build();

//         // OrderItem (SmileOne)
//         OrderItem smileItem = OrderItem.builder()
//                 .id(1001)
//                 .product(smileProduct)
//                 .gameUserId("USER123")
//                 .zoneId("ZONE45")
//                 .delivered(false)
//                 .build();

//         when(orderItemRepository.findByOrderId(order.getId()))
//                 .thenReturn(List.of(smileItem));

//         when(smileOneTpClient.createOrder(any()))
//                 .thenReturn("{\"code\":200,\"message\":\"success\"}");

//         // Run
//         fulfillmentService.processOrder(order);

//         // Capture the SmileOneOrderRequest
//         ArgumentCaptor<SmileOneOrderRequest> captor = ArgumentCaptor.forClass(SmileOneOrderRequest.class);
//         verify(smileOneTpClient).createOrder(captor.capture());

//         SmileOneOrderRequest request = captor.getValue();
//         System.out.println("📦 Sent SmileOne request: " + request);

//         // Assertions (optional)
//         assertEquals("USER123", request.getUserId());
//         assertEquals("ZONE45", request.getZoneId());
//         assertEquals("22590", request.getProductId());
//         assertEquals("Mobile Legends Diamond", request.getProductName());

//         verify(orderItemRepository).save(any());
//         verify(orderRepository).save(any());
//     }
// }
