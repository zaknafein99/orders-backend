package com.kotlin.orders.controller

import com.kotlin.orders.dto.TruckDTO
import com.kotlin.orders.service.TruckService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/truck")
class TruckController(val truckService: TruckService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addTruck(@RequestBody truckDTO: TruckDTO): TruckDTO {
        return truckService.addTruck(truckDTO)
    }

    @PutMapping("/{truck_id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateTruck(@RequestBody truckDTO: TruckDTO, @PathVariable("truck_id") truckId: Int) = truckService.updateTruck(truckId, truckDTO)

    @DeleteMapping("/{truck_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTruck(@PathVariable("truck_id") truckId: Int) = truckService.deleteTruck(truckId)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getTrucks(): List<TruckDTO> = truckService.getTrucks()

}