package com.kotlin.orders.controller

import com.kotlin.orders.dto.CustomerDTO
import com.kotlin.orders.repository.CustomerRepository
import com.kotlin.orders.service.CustomerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/customer")
@Validated
@Tag(name = "Customer", description = "Customer management APIs")
class CustomerController(
    val customerService: CustomerService,
    val customerRepository: CustomerRepository
) {

  @Operation(summary = "Create a new customer")
  @ApiResponses(value = [
    ApiResponse(responseCode = "201", description = "Customer created successfully"),
    ApiResponse(responseCode = "400", description = "Invalid input")
  ])
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  fun addCustomer(@RequestBody @Valid customerDTO: CustomerDTO): CustomerDTO {
    return customerService.addCustomer(customerDTO)
  }

  @Operation(summary = "Update an existing customer")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Customer updated successfully"),
    ApiResponse(responseCode = "404", description = "Customer not found"),
    ApiResponse(responseCode = "400", description = "Invalid input")
  ])
  @PutMapping("/{customer_id}")
  @ResponseStatus(HttpStatus.OK)
  fun updateCustomer(
      @Parameter(description = "Customer DTO for update") @RequestBody @Valid customerDTO: CustomerDTO,
      @Parameter(description = "ID of the customer to update") @PathVariable("customer_id") customerId: Int
  ) = customerService.updateCustomer(customerId, customerDTO)

  @Operation(summary = "Delete a customer")
  @ApiResponses(value = [
    ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
    ApiResponse(responseCode = "404", description = "Customer not found")
  ])
  @DeleteMapping("/{customer_id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun deleteCustomer(@Parameter(description = "ID of the customer to delete") @PathVariable("customer_id") customerId: Int) =
      customerService.deleteCustomer(customerId)

  @Operation(summary = "Get paginated list of customers")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Successfully retrieved customer list")
  ])
  @GetMapping("/list")
  fun getCustomersPaged(
      @Parameter(description = "Pagination parameters") @PageableDefault(page = 0, size = 10) pageable: Pageable
  ): Page<CustomerDTO> {
    return customerService.getCustomersPaged(pageable)
  }

  @Operation(summary = "Create multiple customers")
  @ApiResponses(value = [
    ApiResponse(responseCode = "201", description = "Customers created successfully"),
    ApiResponse(responseCode = "400", description = "Invalid input")
  ])
  @PostMapping("/list")
  @ResponseStatus(HttpStatus.CREATED)
  fun addListOfCustomers(@RequestBody @Valid customerDTO: List<CustomerDTO>): List<CustomerDTO> {
    return customerService.addListOfCustomers(customerDTO)
  }

  @Operation(summary = "Search customer by exact phone number match")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Successfully retrieved customer"),
    ApiResponse(responseCode = "404", description = "No customer found with given phone number")
  ])
  @GetMapping("/by-phone/{phone_number}")
  @ResponseStatus(HttpStatus.OK)
  fun getCustomerByPhoneNumber(
      @Parameter(description = "Phone number to search for") @PathVariable("phone_number") phoneNumber: String
  ): CustomerDTO {
    return customerService.getCustomerByExactPhoneNumber(phoneNumber)
  }
}
