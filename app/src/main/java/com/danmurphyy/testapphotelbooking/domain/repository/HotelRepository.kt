package com.danmurphyy.testapphotelbooking.domain.repository

import com.danmurphyy.testapphotelbooking.domain.model.hotelM.HotelModel
import com.danmurphyy.testapphotelbooking.domain.model.reservationM.ReservationModel
import com.danmurphyy.testapphotelbooking.domain.model.roomM.RoomList
import com.danmurphyy.testapphotelbooking.utils.Resource
import kotlinx.coroutines.flow.Flow

interface HotelRepository {
    fun getHotelInfo(): Flow<Resource<HotelModel>>
    fun getRoomInfo(): Flow<Resource<RoomList>>

    fun getBookingInfo(): Flow<Resource<ReservationModel>>
}