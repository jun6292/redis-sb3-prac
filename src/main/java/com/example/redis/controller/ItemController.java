package com.example.redis.controller;

import com.example.redis.dto.ItemDto;
import com.example.redis.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/{id}/purchase")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purchase(
            @PathVariable("id")
            Long id
    ) {
        itemService.purchase(id);
    }

    @GetMapping("/ranks")
    public List<ItemDto> getRanks() {
        return itemService.getMostSold();
    }

    @PostMapping
    public ItemDto create(
            @RequestBody
            ItemDto itemDto
    ) {
        return itemService.create(itemDto);
    }

    @GetMapping
    public List<ItemDto> readAll() {
        return itemService.readAll();
    }

    @GetMapping("{id}")
    public ItemDto readOne(
            @PathVariable("id")
            Long id
    ) {
        return itemService.readOne(id);
    }

    @PutMapping("{id}")
    public ItemDto update(
            @PathVariable("id")
            Long id,
            @RequestBody
            ItemDto dto
    ) {
        return itemService.update(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable
            Long id
    ) {
        itemService.delete(id);
    }
}
