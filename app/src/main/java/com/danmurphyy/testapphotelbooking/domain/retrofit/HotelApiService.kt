package com.danmurphyy.testapphotelbooking.domain.retrofit

import com.danmurphyy.testapphotelbooking.domain.model.hotelM.HotelModel
import com.danmurphyy.testapphotelbooking.domain.model.reservationM.ReservationModel
import com.danmurphyy.testapphotelbooking.domain.model.roomM.RoomList
import retrofit2.Response
import retrofit2.http.GET

interface HotelApiService {

    @GET("d144777c-a67f-4e35-867a-cacc3b827473")
    suspend fun getAllHotel(): Response<HotelModel>

    @GET("8b532701-709e-4194-a41c-1a903af00195")
    suspend fun getRooms(): Response<RoomList>

    @GET("63866c74-d593-432c-af8e-f279d1a8d2ff")
    suspend fun getBookingInfo(): Response<ReservationModel>
}