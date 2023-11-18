package com.danmurphyy.testapphotelbooking.domain.repository

import android.util.Log
import com.danmurphyy.testapphotelbooking.domain.model.hotelM.HotelModel
import com.danmurphyy.testapphotelbooking.domain.model.reservationM.ReservationModel
import com.danmurphyy.testapphotelbooking.domain.model.roomM.RoomList
import com.danmurphyy.testapphotelbooking.domain.retrofit.HotelApiService
import com.danmurphyy.testapphotelbooking.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import javax.inject.Inject

class HotelRepositoryImp @Inject constructor(private val retrofit: Retrofit) : HotelRepository {
    override fun getHotelInfo(): Flow<Resource<HotelModel>> = flow {
        try {
            val response = retrofit.create(HotelApiService::class.java).getAllHotel()
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    emit(Resource.Success(data))

                } else {
                    emit(Resource.Error("Failed to get data"))
                }
            } else {
                emit(Resource.Error("Failed to fetch data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An Error Occurred"))
        }
    }

    override fun getRoomInfo(): Flow<Resource<RoomList>> = flow {
        try {
            val response = retrofit.create(HotelApiService::class.java).getRooms()
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    emit(Resource.Success(data))

                } else {
                    emit(Resource.Error("Failed to get data"))
                }
            } else {
                emit(Resource.Error("Failed to fetch data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An Error Occurred"))
        }
    }

    override fun getBookingInfo(): Flow<Resource<ReservationModel>> = flow {
        try {
            val response = retrofit.create(HotelApiService::class.java).getBookingInfo()
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    emit(Resource.Success(data))
                    Log.d("MainViewModel", "getBookingInfo: $data")

                } else {
                    emit(Resource.Error("Failed to get data"))
                }
            } else {
                emit(Resource.Error("Failed to fetch data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An Error Occurred"))
        }
    }
}