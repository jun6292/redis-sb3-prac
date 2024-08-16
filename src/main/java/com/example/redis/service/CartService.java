package com.example.redis.service;

import com.example.redis.dto.CartDto;
import com.example.redis.dto.CartItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

//로그인 없는 장바구니 기능
//
// 1. 데이터 타입은 Hash를 사용
// 2. 특정 사용자의 장바구니가 사용된지 3시간이 지나면 삭제되도록 조정
// 3. 장바구니에 물품 조정, 장바구니 조회 기능이 존재
//     a. 특별한 Entity의 추가 구현 없이, 대상 물품과 수량은 클라이언트가 정해서 전달한다고 가정
//     b. 만약 수량을 줄이고 싶다면 음수가 전달되며,
//     c. 수량이 0 이하가 되면 장바구니에서 제거된다.
// 4. 여러 애플리케이션 인스턴스에 걸쳐 부하가 분산됨을 가정

@Slf4j
@Service
public class CartService {
    private final String keyString = "cart:%s";
    private final RedisTemplate<String, String> cartTemplate;
    private final HashOperations<String, String, Integer> hashOps;

    public CartService(RedisTemplate<String, String> cartTemplate) {
        this.cartTemplate = cartTemplate;
        this.hashOps = this.cartTemplate.opsForHash();
    }

    public void modifyCart(String sessionId, CartItemDto dto) {
        hashOps.increment(
                keyString.formatted(sessionId),
                dto.getItem(),
                dto.getCount()
        );
        int count = Optional.ofNullable(hashOps.get(keyString.formatted(sessionId), dto.getItem()))
                .orElse(0);
        if (count <= 0) {
            hashOps.delete(keyString.formatted(sessionId), dto.getItem());
        }
    }

    public CartDto getCart(String sessionId) {
        boolean exists = Optional.ofNullable(cartTemplate.hasKey(keyString.formatted(sessionId)))
                .orElse(false);
        if (!exists)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Date expireAt = Date.from(Instant.now().plus(30, ChronoUnit.SECONDS));
        cartTemplate.expireAt(
                keyString.formatted(sessionId),
                expireAt
        );
        return CartDto.fromHashPairs(
                hashOps.entries(keyString.formatted(sessionId)),
                expireAt
        );
    }
}

