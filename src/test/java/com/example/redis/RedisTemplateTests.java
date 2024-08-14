package com.example.redis;

import com.example.redis.dto.ItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTemplateTests {
    @Autowired
    // StringRedisTemplate인 이유는 자바에서 쓰는 타입이 String이라서.
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void stringOpsTests() {
        // 자바에서 레디스의 문자열 조작을 위한 클래스
        // redis template에 설정된 타입을 바탕으로 redis의 문자열 조작을 한다.
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("simple key", "simple values");
        System.out.println(ops.get("simple key"));

        // 자바에서 레디스의 집합 조작을 위한 클래스
        SetOperations<String, String> setOps = stringRedisTemplate.opsForSet();
        setOps.add("hobbies", "games");
        setOps.add("hobbies", "coding", "alcohol", "games");
        System.out.println(setOps.size("hobbies"));

        stringRedisTemplate.expire("hobbies", 10, TimeUnit.SECONDS);
        stringRedisTemplate.delete("simple key");
    }

    @Autowired
    private RedisTemplate<String, ItemDto> itemRedisTemplate;

    @Test
    public void itemRedisTemplateTests() {
        ValueOperations<String, ItemDto> ops = itemRedisTemplate.opsForValue();
        ops.set("my:keyboard", ItemDto.builder()
                        .name("Mechanical keyboard")
                        .description("very expensive keyboard")
                        .price(300000)
                .build());
        System.out.println(ops.get("my:keyboard"));
    }
}
