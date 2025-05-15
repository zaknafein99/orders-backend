package com.kotlin.orders.service

import com.kotlin.orders.dto.TruckDTO
import com.kotlin.orders.entity.Truck
import com.kotlin.orders.exceptionhandler.TruckNotFoundException
import com.kotlin.orders.repository.TruckRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class TruckService(val truckRepository: TruckRepository){

    companion object : KLogging()

    fun addTruck(truckDTO: TruckDTO): TruckDTO {
        val truckEntity = Truck(null, truckDTO.name)
        truckRepository.save(truckEntity)
        logger.info { "Truck added: $truckEntity" }

        return truckEntity.let {
            TruckDTO(it.id, it.name)
        }

    }

    fun getTrucks(): List<TruckDTO> {

        return truckRepository.findAll().map {
            TruckDTO(it.id, it.name)
        }

    }

    fun updateTruck(truckId: Int, truckDTO: TruckDTO): TruckDTO {

            val existingTruck = truckRepository.findById(truckId)
            return if(existingTruck.isPresent){
                existingTruck.let {
                    it.get().name = truckDTO.name
                    truckRepository.save(it.get())
                    TruckDTO(it.get().id, it.get().name)
                }
            }else{
                throw TruckNotFoundException("Truck not found with id: $truckId")
            }

    }

    fun deleteTruck(truckId: Int){

        val existingTruck = truckRepository.findById(truckId)
        if(existingTruck.isPresent){
            truckRepository.deleteById(truckId)
        }else{
            throw TruckNotFoundException("Truck not found with id: $truckId")
        }

    }

    fun getTruckById(truckId: Int): TruckDTO {

            val existingTruck = truckRepository.findById(truckId)
            return if(existingTruck.isPresent){
                existingTruck.let {
                    TruckDTO(it.get().id, it.get().name)
                }
            }else{
                throw TruckNotFoundException("Truck not found with id: $truckId")
            }

    }

    fun findById(id: Int): Truck? {
        return truckRepository.findById(id).orElse(null)
    }

}
