package com.kotlin.orders.mapper

import com.kotlin.orders.dto.OrderDTO
import com.kotlin.orders.entity.Order
import com.kotlin.orders.entity.Item
import com.kotlin.orders.entity.OrderItem
import com.kotlin.orders.dto.ItemDTO
import org.mapstruct.*

@Mapper(
    componentModel = "spring",
    uses = [CustomerMapper::class, ItemMapper::class, TruckMapper::class],
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface OrderMapper {

    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "truck", source = "truck")
    fun toDto(order: Order): OrderDTO

    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "orderItems", ignore = true) // Will be handled in service
    @Mapping(target = "truck", source = "truck")
    fun toEntity(orderDTO: OrderDTO): Order

    fun toDtoList(orderList: List<Order>): List<OrderDTO>

    fun toEntityList(orderDTOList: List<OrderDTO>): List<Order>
    
    @Named("mapOrderItemsToItemDTOs")
    @IterableMapping(qualifiedByName = ["mapOrderItemToItemDTO"])
    fun mapOrderItemsToItemDTOs(orderItems: List<OrderItem>): List<ItemDTO>
    
    @Named("mapOrderItemToItemDTO")
    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name", defaultValue = "Unknown Item")
    @Mapping(target = "description", source = "item.description", defaultValue = "")
    @Mapping(target = "price", source = "item.price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "category", source = "item.category", defaultValue = "Uncategorized")
    fun mapOrderItemToItemDTO(orderItem: OrderItem): ItemDTO
}