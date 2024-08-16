package com.example.redis.service;

import com.example.redis.dto.ItemDto;
import com.example.redis.entity.Item;
import com.example.redis.entity.ItemOrder;
import com.example.redis.repository.ItemRepository;
import com.example.redis.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final ZSetOperations<String, ItemDto> rankOps;

    public ItemService(
            ItemRepository itemRepository,
            OrderRepository orderRepository,
            RedisTemplate<String, ItemDto> rankTemplate
    ) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.rankOps = rankTemplate.opsForZSet();
    }

    public void purchase(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        orderRepository.save(ItemOrder.builder()
                .item(item)
                .count(1)
                .build());
        ItemDto dto = ItemDto.fromEntity(item);
        rankOps.incrementScore("soldRanks", dto, 1);
    }

    public List<ItemDto> getMostSold() {
        Set<ItemDto> ranks = rankOps.reverseRange("soldRanks", 0, 9);
        if (ranks == null) {
            return Collections.emptyList();
        }
        return ranks.stream().toList();
    }

    @CachePut(cacheNames = "itemCache", key = "#result.id")
    public ItemDto create(ItemDto dto) {
        return ItemDto.fromEntity(itemRepository.save(Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build()));
    }

    @Cacheable(cacheNames = "itemAllCache", key="methodName")
    public List<ItemDto> readAll() {
        return itemRepository.findAll()
                .stream()
                .map(ItemDto::fromEntity)
                .toList();
    }

    // 이 메서드의 결과는 캐싱이 가능하다
    // cacheNames: 해당 메서드로 인해서 만들어질 캐시를 지칭하는 이름
    // key: 캐시 데이터를 구분하기 위해 활용하는 값
    @Cacheable(cacheNames = "itemCache", key = "args[0]")
    public ItemDto readOne(Long id) {
        return itemRepository.findById(id)
                .map(ItemDto::fromEntity)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @CachePut(cacheNames = "itemCache", key = "args[0]")
    @CacheEvict(cacheNames = "itemAllCache", allEntries = true)
    public ItemDto update(Long id, ItemDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        return ItemDto.fromEntity(itemRepository.save(item));
    }

    // #id는 메서드의 인자 이름을 참조하는 방식
    @Caching(evict = {
            @CacheEvict(cacheNames = "itemAllCache", allEntries = true),
            @CacheEvict(cacheNames = "itemCache", key= "#id")
    })
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Cacheable(
            cacheNames = "itemSearchCache",
            key = "{ args[0], args[1].pageNumber, args[1].pageSize }"
    )
    public Page<ItemDto> searchByName(String query, Pageable pageable) {
        return itemRepository.findAllByNameContains(query, pageable)
                .map(ItemDto::fromEntity);
    }

}
