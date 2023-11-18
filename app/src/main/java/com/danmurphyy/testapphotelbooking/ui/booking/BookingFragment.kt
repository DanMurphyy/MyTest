package com.danmurphyy.testapphotelbooking.ui.booking

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.danmurphyy.testapphotelbooking.IViewHandler
import com.danmurphyy.testapphotelbooking.MainActivity
import com.danmurphyy.testapphotelbooking.R
import com.danmurphyy.testapphotelbooking.databinding.FragmentBookingBinding
import com.danmurphyy.testapphotelbooking.databinding.TouristLayoutBinding
import com.danmurphyy.testapphotelbooking.ui.MainViewModel
import com.danmurphyy.testapphotelbooking.utils.EmailValidator
import com.danmurphyy.testapphotelbooking.utils.priceFormat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class BookingFragment : Fragment(), IViewHandler {
    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var touristLayoutCounter = 2
    private val touristLayoutManagers = mutableListOf<TouristLayoutManager>()
    private lateinit var topAppBar: MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)

        navigateBack()
        emailError()
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            if (viewModel.hasInternetConnection()) {
                showInfoItems()
                hideErrorMessage()
            } else {
                showErrorMessage()
            }
        }

        binding.addBtnImage.setOnClickListener { addTouristLayout() }

        binding.includedTouristLayout.arrowDownBtn.setOnClickListener {
            toggleLayoutVisibility(binding.includedTouristLayout.llTouristInfo)
        }

        binding.includedTouristLayout.arrowUpBtn.setOnClickListener {
            toggleLayoutVisibility(binding.includedTouristLayout.llTouristInfo)
        }

        binding.btnPayBooking.setOnClickListener {
            if (isTouristLayoutEmpty()) {
                showEmptyFieldErrors()
                Toast.makeText(
                    requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(R.id.action_bookingFragment_to_resultFragment)
            }
        }

        // Set OnClickListener for date of birth TextInputEditText
        binding.includedTouristLayout.edDob.setOnClickListener {
            showDatePicker()
        }

        binding.includedTouristLayout.edValid.setOnClickListener {
            showDatePickerForValidDate()
        }

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.hasInternetConnection()) {
            showInfoItems()
            hideErrorMessage()
        } else {
            showErrorMessage()
        }
    }

    private fun addTouristLayout() {
        val touristLayoutBinding =
            TouristLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        val touristLayoutManager = TouristLayoutManager(touristLayoutBinding)
        touristLayoutCounter++
        touristLayoutManagers.add(touristLayoutManager)
        binding.includedTouristLayout.llTourist.addView(touristLayoutBinding.root)
    }

    private inner class TouristLayoutManager(binding: TouristLayoutBinding) {
        private val arrowDownBtn = binding.arrowDownBtn
        private val arrowUpBtn = binding.arrowUpBtn
        private val llTouristInfo = binding.llTouristInfo
        private val touristNumberTextView = binding.touristNumber

        init {
            updateTouristNumber()
            arrowDownBtn.setOnClickListener {
                clickedVisibility(
                    LinearLayout.GONE, ImageView.GONE, ImageView.VISIBLE
                )

            }
            arrowUpBtn.setOnClickListener {
                clickedVisibility(
                    LinearLayout.VISIBLE, ImageView.VISIBLE, ImageView.GONE
                )
            }
        }

        private fun updateTouristNumber() {
            touristNumberTextView.text = buildString {
                append(convertNumberToOrdinalText(touristLayoutCounter))
                append(" ")
                append(getString(R.string.tourist))
            }
        }

        private fun clickedVisibility(
            layoutVisibility: Int,
            downVisibility: Int,
            upVisibility: Int,
        ) {
            llTouristInfo.visibility = layoutVisibility
            arrowDownBtn.visibility = downVisibility
            arrowUpBtn.visibility = upVisibility
        }

        private fun convertNumberToOrdinalText(number: Int): String {
            return when (number) {
                1 -> "Первый"
                2 -> "Второй"
                3 -> "Третий"
                4 -> "Четвертый"
                // Add more cases as needed
                else -> "$number-й"  // Default to using the numeric suffix
            }
        }
    }

    private fun emailError() {
        val emailInputLayout = binding.emailInputLayout
        val edEmail = binding.edEmail

        edEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail(edEmail.text.toString(), emailInputLayout)
            }
        }
    }

    private fun validateEmail(email: String, inputLayout: TextInputLayout) {
        val emailValidator = EmailValidator()

        if (emailValidator.isValidEmail(email)) {
            inputLayout.error = null
            inputLayout.isErrorEnabled = false
        } else {
            inputLayout.error = getString(R.string.error_input_email)
        }
    }

    private fun showInfoItems() {
        viewModel.fetchBookingInfo(this)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.reservationModel.collectLatest { resModel ->
                withContext(Dispatchers.Main) {
                    binding.hotelName.text = resModel?.hotel_name
                    binding.hotelInfoAdress.text = resModel?.hotel_adress
                    binding.hotelInfoRating.text = resModel?.horating?.toString() ?: "5"
                    binding.hotelInfoRatingName.text =
                        resModel?.rating_name ?: getString(R.string.perfect)
                    binding.bookingDeparture.text = resModel?.departure
                    binding.bookingArrival.text = resModel?.arrival_country
                    binding.bookingDateStart.text = resModel?.tour_date_start
                    binding.bookingHotelName.text = resModel?.hotel_name
                    binding.bookingDateStop.text = resModel?.tour_date_stop
                    binding.bookingNights.text = resModel?.number_of_nights.toString()
                    binding.bookingRoom.text = resModel?.room
                    binding.bookingNutrition.text = resModel?.nutrition
                    binding.bookingTourPrice.text =
                        resModel?.tour_price?.let { it1 -> priceFormat(it1) }
                    binding.bookingFuelCharge.text =
                        resModel?.fuel_charge?.let { it1 -> priceFormat(it1) }
                    binding.bookingServiceCharge.text =
                        resModel?.service_charge?.let { it1 -> priceFormat(it1) }
                    val total = resModel?.let {
                        it.tour_price + it.fuel_charge + it.service_charge
                    } ?: 0.0
                    binding.bookingFinalRate.text = priceFormat(total.toInt())
                    binding.totalBtn.text = priceFormat(total.toInt())
                }
            }
        }
    }

    private fun toggleLayoutVisibility(layout: LinearLayout) {
        layout.visibility = if (layout.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }

        val arrowDownBtnVisibility =
            if (layout.visibility == View.VISIBLE) View.VISIBLE else View.GONE
        val arrowUpBtnVisibility =
            if (layout.visibility == View.VISIBLE) View.GONE else View.VISIBLE

        binding.includedTouristLayout.arrowDownBtn.visibility = arrowDownBtnVisibility
        binding.includedTouristLayout.arrowUpBtn.visibility = arrowUpBtnVisibility
    }

    private fun isTouristLayoutEmpty(): Boolean {
        val phoneNumber = binding.edPhoneNumber
        val emailAddress = binding.edEmail
        val nameEditText = binding.includedTouristLayout.edName
        val surnameEditText = binding.includedTouristLayout.edSurname
        val dobEditText = binding.includedTouristLayout.edDob
        val nationalitySpinner = binding.includedTouristLayout.spinnerNationality
        val passportNumberEditText = binding.includedTouristLayout.edPasNumber
        val validDateEditText = binding.includedTouristLayout.edValid
        return phoneNumber.text.isNullOrBlank() || emailAddress.text.isNullOrBlank() ||
                nameEditText.text.isNullOrBlank() || surnameEditText.text.isNullOrBlank() ||
                dobEditText.text.isNullOrBlank() ||
                nationalitySpinner.selectedItemPosition == AdapterView.INVALID_POSITION ||
                passportNumberEditText.text.isNullOrBlank() ||
                validDateEditText.text.isNullOrBlank()
    }

    private fun showEmptyFieldErrors() {
        val phoneNumber = binding.edPhoneNumber
        val emailAddress = binding.edEmail
        val nameEditText = binding.includedTouristLayout.edName
        val surnameEditText = binding.includedTouristLayout.edSurname
        val dobEditText = binding.includedTouristLayout.edDob
        val nationalitySpinner = binding.includedTouristLayout.spinnerNationality
        val passportNumberEditText = binding.includedTouristLayout.edPasNumber
        val validDateEditText = binding.includedTouristLayout.edValid

        val phoneNumberLayout = binding.textInputLayoutPhoneNumber
        val emailAddressLayout = binding.emailInputLayout
        val nameInputLayout = binding.includedTouristLayout.textInputLayoutName
        val surnameInputLayout = binding.includedTouristLayout.textInputLayoutSurname
        val dobInputLayout = binding.includedTouristLayout.textInputLayoutDob
        val nationalityInputLayout = binding.includedTouristLayout.textInputLayoutNationality
        val passportNumberInputLayout = binding.includedTouristLayout.textInputLayoutPasNumber
        val validDateInputLayout = binding.includedTouristLayout.textInputLayoutValid

        if (phoneNumber.text.isNullOrBlank()) {
            phoneNumberLayout.error = getString(R.string.error_input_phone_number)
        } else {
            phoneNumberLayout.error = null
            phoneNumberLayout.isErrorEnabled = false

        }

        if (emailAddress.text.isNullOrBlank()) {
            emailAddressLayout.error = getString(R.string.error_input_email)
        } else {
            emailAddressLayout.error = null
            emailAddressLayout.isErrorEnabled = false

        }

        if (nameEditText.text.isNullOrBlank()) {
            nameInputLayout.error = getString(R.string.error_input_name)
        } else {
            nameInputLayout.error = null
            nameInputLayout.isErrorEnabled = false

        }

        if (surnameEditText.text.isNullOrBlank()) {
            surnameInputLayout.error = getString(R.string.error_input_surname)
        } else {
            surnameInputLayout.error = null
            surnameInputLayout.isErrorEnabled = false

        }

        if (dobEditText.text.isNullOrBlank()) {
            dobInputLayout.error = getString(R.string.error_input_dateOfBirth)
        } else {
            dobInputLayout.error = null
            dobInputLayout.isErrorEnabled = false

        }

        if (nationalitySpinner.selectedItemPosition == 0) {
            nationalityInputLayout.error = getString(R.string.error_input_citizenship)
        } else {
            nationalityInputLayout.error = null
            nationalityInputLayout.isErrorEnabled = false

        }

        if (passportNumberEditText.text.isNullOrBlank()) {
            passportNumberInputLayout.error = getString(R.string.error_input_numberPassport)
        } else {
            passportNumberInputLayout.error = null
            passportNumberInputLayout.isErrorEnabled = false

        }

        if (validDateEditText.text.isNullOrBlank()) {
            validDateInputLayout.error = getString(R.string.error_input_validityPeriod)
        } else {
            validDateInputLayout.error = null
            validDateInputLayout.isErrorEnabled = false

        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Handle the chosen date
                val chosenDate = Calendar.getInstance()
                chosenDate.set(year, monthOfYear, dayOfMonth)
                updateDobText(chosenDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showDatePickerForValidDate() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val chosenDate = Calendar.getInstance()
                chosenDate.set(year, monthOfYear, dayOfMonth)
                if (chosenDate.after(Calendar.getInstance())) {
                    updateValidDateText(chosenDate.time)
                } else {
                    Toast.makeText(requireContext(), "Choose a future date", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun updateDobText(date: Date) {
        binding.includedTouristLayout.edDob.setText(formatDate(date))
    }

    private fun updateValidDateText(date: Date) {
        binding.includedTouristLayout.edValid.setText(formatDate(date))
    }

    override fun showProgressBar() {
        binding.ProgressBar.visibility = View.VISIBLE
        binding.llBooking.visibility = View.GONE
        super.showProgressBar()
    }

    override fun hideProgressBar() {
        binding.ProgressBar.visibility = View.GONE
        binding.llBooking.visibility = View.VISIBLE
        super.hideProgressBar()
    }

    override fun showError(error: String) {
        binding.llBooking.visibility = View.GONE
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        super.showError(error)

    }

    private fun showErrorMessage() {
        binding.itemErrorMessage.root.visibility = View.VISIBLE
        binding.llBooking.visibility = View.GONE
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.root.visibility = View.GONE
        binding.llBooking.visibility = View.VISIBLE
    }

    private fun navigateBack() {
        topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            (requireActivity() as MainActivity).handleBackPress()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}