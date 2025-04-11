package com.kotlin.orders.mapper

import com.kotlin.orders.dto.CustomerDTO
import com.kotlin.orders.entity.Customer
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface CustomerMapper {

    @Mappings(Mapping(target = "orders", expression = "java(java.util.Collections.emptyList())"))
    fun customerDTOToCustomer(customerDTO: CustomerDTO): Customer

    fun customerToCustomerDTO(customer: Customer?): CustomerDTO

    @Mappings(Mapping(target = "orders", expression = "java(java.util.Collections.emptyList())"))
    fun customerDTOListToCustomerList(customerDTOList: List<CustomerDTO>): List<Customer>

    fun customerListToCustomerDTOList(customerList: List<Customer>): List<CustomerDTO>

}