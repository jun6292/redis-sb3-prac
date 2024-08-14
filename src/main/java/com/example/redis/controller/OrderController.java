package com.example.redis.controller;

import com.example.redis.entity.ItemOrder;
import com.example.redis.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("orders")
public class OrderController {
    private final OrderRepository orderRepository;

    @PostMapping
    public ItemOrder create(
            @RequestBody
            ItemOrder order
    ) {
        return orderRepository.save(order);
    }

    @GetMapping
    public List<ItemOrder> readAll() {
        List<ItemOrder> orders = new ArrayList<>();
        orderRepository.findAll()
                .forEach(orders::add);
        return orders;
    }

    @GetMapping("/{id}")
    public ItemOrder readOne(
            @PathVariable("id")
            Long id
    ) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

//    @PutMapping("/{id}")
//    public ItemOrder update(
//            @PathVariable("id")
//            Long id,
//            @RequestBody
//            ItemOrder order
//    ) {
//        ItemOrder target = orderRepository
//                .findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        target.setItem(order.getItem());
//        target.setCount(order.getCount());
//        target.setTotalPrice(order.getTotalPrice());
//        target.setStatus(order.getStatus());
//        return orderRepository.save(target);
//    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id")
            Long id
    ) {
        orderRepository.deleteById(id);
    }
}
