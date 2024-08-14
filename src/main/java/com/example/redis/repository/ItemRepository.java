package com.example.redis.repository;

import com.example.redis.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {


}
