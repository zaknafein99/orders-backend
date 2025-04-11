package com.kotlin.orders.mapper

import com.kotlin.orders.dto.TruckDTO
import com.kotlin.orders.entity.Truck
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface TruckMapper {
    fun toDto(truck: Truck): TruckDTO
    fun toEntity(truckDTO: TruckDTO): Truck
}
