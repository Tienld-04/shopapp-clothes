package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.dto.ShoppingCart.CartItemRequest;
import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import com.example.shop_clothes.model.*;
import com.example.shop_clothes.repository.*;
import com.example.shop_clothes.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public String addItemToShoppingCart(CartItemRequest cartItemRequest) {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user);
        if (shoppingCart == null) {
            throw new ApplicationException(ErrorCode.SHOPPINGCART_NOT_EXISTED);
        }
        Product product = productRepository.findById(cartItemRequest.getProductId()).get();
        ProductDetail productDetail = productDetailRepository.findById(cartItemRequest.getProductDetailId()).get();
        if (productDetail.getQuantity() == 0) {
            throw new RuntimeException("không đủ hàng trong kho");
        }
        CartItem cartItem = cartItemRepository.findByProductDetail_Id(cartItemRequest.getProductDetailId());
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(shoppingCart);
            cartItem.setQuantity(1);
            cartItem.setPrice(product.getPrice() + productDetail.getPriceAdjustment());
            cartItem.setProduct(product);
            cartItem.setProductDetail(productDetail);
            cartItemRepository.save(cartItem);
        }
        return "Thêm sản phẩm vào giỏ thành công";
    }
}
