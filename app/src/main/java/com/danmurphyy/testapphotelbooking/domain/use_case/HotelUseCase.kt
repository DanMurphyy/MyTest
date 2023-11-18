package com.danmurphyy.testapphotelbooking.domain.use_case

import com.danmurphyy.testapphotelbooking.domain.model.hotelM.HotelModel
import com.danmurphyy.testapphotelbooking.domain.model.reservationM.ReservationModel
import com.danmurphyy.testapphotelbooking.domain.model.roomM.RoomList
import com.danmurphyy.testapphotelbooking.domain.repository.HotelRepository
import com.danmurphyy.testapphotelbooking.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HotelUseCase @Inject constructor(private val hotelRepository: HotelRepository) {
    fun getAllHotels(): Flow<Resource<HotelModel>> = hotelRepository.getHotelInfo()
    fun getAllRooms(): Flow<Resource<RoomList>> = hotelRepository.getRoomInfo()

    fun getBookingInfo(): Flow<Resource<ReservationModel>> = hotelRepository.getBookingInfo()
}