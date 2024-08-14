package com.example.redis;

import com.example.redis.entity.Item;
import com.example.redis.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisRepositoryTests {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createTest() {
        Item item = Item.builder()
                .name("keyboard")
                .description("Very Expensive Keyboard")
                .price(100000)
                .build();
        itemRepository.save(item);
    }

    @Test
    public void readOneTest() {
        Item item = itemRepository.findById("80a0b63f-cec2-484c-8f8f-5882603d3657").orElseThrow();
        System.out.println(item.getDescription());
    }

    @Test
    public void updateTest() {
        Item item = itemRepository.findById("80a0b63f-cec2-484c-8f8f-5882603d3657").orElseThrow();
        item.setDescription("on sale!!!");
        itemRepository.save(item);
        item = itemRepository.save(item);
        System.out.println(item.getDescription());
    }

    @Test
    public void deleteTest() {
        itemRepository.deleteById("80a0b63f-cec2-484c-8f8f-5882603d3657");
    }
}
