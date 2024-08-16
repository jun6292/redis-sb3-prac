package com.example.redis.controller;

import com.example.redis.dto.CartDto;
import com.example.redis.dto.CartItemDto;
import com.example.redis.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public CartDto modifyCart(
            @RequestBody
            CartItemDto itemDto,
            HttpSession session
    ) {
        cartService.modifyCart(session.getId(), itemDto);
        return cartService.getCart(session.getId());
    }

    @GetMapping
    public CartDto getCart(
            HttpSession session
    ) {
        log.info(session.getId());
        return cartService.getCart(session.getId());
    }
}

