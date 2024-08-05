package com.example.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
// Entity 대신 RedisHash를 사용하여 Redis에 저장할 수 있도록 설정
@RedisHash("item")
public class Item implements Serializable {
    @Id
    // id를 String으로 설정하면 UUID가 자동으로 배정된다.
    private String id;
    private String name;
    private String description;
    private Integer price;
}
