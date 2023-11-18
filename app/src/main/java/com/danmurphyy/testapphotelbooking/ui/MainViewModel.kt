package com.danmurphyy.testapphotelbooking.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.danmurphyy.testapphotelbooking.IViewHandler
import com.danmurphyy.testapphotelbooking.domain.model.hotelM.HotelModel
import com.danmurphyy.testapphotelbooking.domain.model.reservationM.ReservationModel
import com.danmurphyy.testapphotelbooking.domain.model.roomM.RoomList
import com.danmurphyy.testapphotelbooking.domain.use_case.HotelUseCase
import com.danmurphyy.testapphotelbooking.ui.room.RoomFragment
import com.danmurphyy.testapphotelbooking.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val hotelUseCase: HotelUseCase,
    application: Application,
) : AndroidViewModel(application) {

    private lateinit var iViewHandler: IViewHandler

    private var _hotelModel: MutableStateFlow<HotelModel?> = MutableStateFlow(null)
    val hotelModel: StateFlow<HotelModel?> = _hotelModel

    private var _roomModelList = MutableStateFlow(RoomList(emptyList()))
    val roomModelList: StateFlow<RoomList> = _roomModelList

    private var _reservationModel: MutableStateFlow<ReservationModel?> = MutableStateFlow(null)
    val reservationModel: StateFlow<ReservationModel?> = _reservationModel

    fun fetchAllHotels(listener: IViewHandler) = viewModelScope.launch {
        iViewHandler = listener
        hotelUseCase.getAllHotels().collectLatest {
            when (it) {
                is Resource.Success -> {
                    iViewHandler.hideProgressBar()
                    _hotelModel.value = it.data
                }

                is Resource.Loading -> {
                    iViewHandler.showProgressBar()
                }

                is Resource.Error -> {
                    iViewHandler.showError(it.message ?: "An Error Occurred")
                }
            }
        }
    }

    fun fetchAllRooms(listener: RoomFragment) = viewModelScope.launch {
        iViewHandler = listener
        hotelUseCase.getAllRooms().collectLatest {
            when (it) {
                is Resource.Success -> {
                    iViewHandler.hideProgressBar()
                    _roomModelList.value = it.data
                }

                is Resource.Loading -> {
                    iViewHandler.showProgressBar()
                }

                is Resource.Error -> {
                    iViewHandler.showError(it.message ?: "An Error Occurred")
                }
            }
        }
    }

    fun fetchBookingInfo(listener: IViewHandler) = viewModelScope.launch {
        iViewHandler = listener
        hotelUseCase.getBookingInfo().collectLatest {
            when (it) {
                is Resource.Success -> {
                    iViewHandler.hideProgressBar()
                    _reservationModel.value = it.data
                }

                is Resource.Loading -> {
                    iViewHandler.showProgressBar()
                }

                is Resource.Error -> {
                    iViewHandler.showError(it.message ?: "An Error Occurred")
                }
            }
        }
    }

    fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}