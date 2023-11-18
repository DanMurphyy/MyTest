package com.danmurphyy.testapphotelbooking.ui.hotel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.danmurphyy.testapphotelbooking.IViewHandler
import com.danmurphyy.testapphotelbooking.R
import com.danmurphyy.testapphotelbooking.databinding.FragmentHotelBinding
import com.danmurphyy.testapphotelbooking.ui.MainViewModel
import com.danmurphyy.testapphotelbooking.utils.priceFormat
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HotelFragment : Fragment(), IViewHandler {
    private var _binding: FragmentHotelBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHotelBinding.inflate(inflater, container, false)

        binding.itemErrorMessage.btnRetry.setOnClickListener {
            if (viewModel.hasInternetConnection()) {
                showItems()
                hideErrorMessage()
            } else {
                showErrorMessage()
            }
        }

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.hasInternetConnection()) {
            showItems()
            hideErrorMessage()
        } else {
            showErrorMessage()
        }
    }

    private fun showItems() {
        viewModel.fetchAllHotels(this)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.hotelModel.collectLatest { hotel ->
                withContext(Dispatchers.Main) {
                    binding.hotelName.text = hotel?.name
                    binding.hotelInfoAdress.text = hotel?.adress
                    binding.hotelInfoMinimalPrice.text =
                        hotel?.minimal_price?.let { it1 -> priceFormat(it1) }
                    binding.hotelInfoPriceForIt.text = hotel?.price_for_it
                    binding.hotelInfoRating.text = hotel?.rating?.toString() ?: "5"
                    binding.hotelInfoRatingName.text =
                        hotel?.rating_name ?: getString(R.string.perfect)
                    binding.hotelInfoDescription.text = hotel?.about_the_hotel?.description
                    binding.hotelInfoPeculiarities1.text =
                        hotel?.about_the_hotel?.peculiarities?.get(0)
                    binding.hotelInfoPeculiarities2.text =
                        hotel?.about_the_hotel?.peculiarities?.get(1)
                    binding.hotelInfoPeculiarities3.text =
                        hotel?.about_the_hotel?.peculiarities?.get(2)
                    binding.hotelPeculiarities4.text =
                        hotel?.about_the_hotel?.peculiarities?.get(3)
                    val slideModels = hotel?.image_urls?.map { url ->
                        SlideModel(url)
                    } ?: emptyList()

                    binding.hotelImageSlider.setImageList(slideModels)

                    binding.hotelButton.setOnClickListener {
                        val hotelName = binding.hotelName.text.toString()
                        hotelToRoomFragment(hotelName)
                    }
                }
            }
        }
    }

    private fun hotelToRoomFragment(name: String) {
        findNavController().navigate(HotelFragmentDirections.actionHotelFragmentToRoomFragment(name))
    }

    override fun showProgressBar() {
        binding.ProgressBar.visibility = View.VISIBLE
        binding.clHotel.visibility = View.GONE
        super.showProgressBar()
    }

    override fun hideProgressBar() {
        binding.ProgressBar.visibility = View.GONE
        binding.clHotel.visibility = View.VISIBLE
        super.hideProgressBar()
    }

    override fun showError(error: String) {
        binding.clHotel.visibility = View.GONE
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        super.showError(error)

    }

    private fun showErrorMessage() {
        binding.itemErrorMessage.root.visibility = View.VISIBLE
        binding.clHotel.visibility = View.GONE
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.root.visibility = View.GONE
        binding.clHotel.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}