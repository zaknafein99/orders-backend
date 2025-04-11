package com.kotlin.orders.mapper

import com.kotlin.orders.dto.ItemDTO
import com.kotlin.orders.entity.Item
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface ItemMapper {
    fun toDto(item: Item): ItemDTO
    fun toEntity(itemDTO: ItemDTO): Item
    fun toDtoList(items: List<Item>): List<ItemDTO>
    fun toEntityList(itemDTOs: List<ItemDTO>): List<Item>
}
