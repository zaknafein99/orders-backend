package com.kotlin.orders.controller

import com.kotlin.orders.dto.ItemDTO
import com.kotlin.orders.repository.ItemRepository
import com.kotlin.orders.service.ItemService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/item")
@Validated
class ItemController(val itemService: ItemService, val itemRepository: ItemRepository) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addItem(@RequestBody @Valid itemDto: ItemDTO): ItemDTO {
        return itemService.addItem(itemDto)
    }

    @PutMapping("/{item_id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateItem(@RequestBody itemDTO: ItemDTO, @PathVariable("item_id") itemId: Int) = itemService.updateItem(itemId, itemDTO)

    @DeleteMapping("/{item_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteItem(@PathVariable("item_id") itemId: Int) = itemService.deleteItem(itemId)

    @GetMapping("/{item_id}")
    @ResponseStatus(HttpStatus.OK)
    fun getItemById(@PathVariable("item_id") itemId: Int): ItemDTO {
        val item = itemService.findById(itemId) 
            ?: throw RuntimeException("Item not found with id $itemId")
        return ItemDTO(item.id, item.name, item.description, item.price, item.quantity, item.category)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getItems(): List<ItemDTO> = itemService.getItems()

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    fun getItemsPaged(@PageableDefault(page = 0, size = 10, sort = ["name"]) pageable: Pageable) : Page<ItemDTO>{
        return itemService.findAll(pageable)
    }

    @PostMapping("/list")
    @ResponseStatus(HttpStatus.CREATED)
    fun addListOfItems (@RequestBody @Valid itemDTO: List<ItemDTO>): List<ItemDTO> {
        return itemService.addListOfItems(itemDTO)
    }
}