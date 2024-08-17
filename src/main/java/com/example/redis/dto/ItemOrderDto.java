package com.example.redis.dto;

import com.example.redis.entity.ItemOrder;
import lombok.*;

import java.io.Serializable;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrderDto implements Serializable {
    private Long id;
    private Long itemId;
    private Integer count;


    public static ItemOrderDto fromEntity(ItemOrder entity) {
        return ItemOrderDto.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .count(entity.getCount())
                .build();
    }
}

