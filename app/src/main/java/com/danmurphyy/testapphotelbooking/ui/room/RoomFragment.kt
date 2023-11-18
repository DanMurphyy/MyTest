package com.danmurphyy.testapphotelbooking.ui.room

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.danmurphyy.testapphotelbooking.IViewHandler
import com.danmurphyy.testapphotelbooking.MainActivity
import com.danmurphyy.testapphotelbooking.R
import com.danmurphyy.testapphotelbooking.databinding.FragmentRoomBinding
import com.danmurphyy.testapphotelbooking.ui.MainViewModel
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RoomFragment : Fragment(), IViewHandler {
    private var _binding: FragmentRoomBinding? = null
    private val binding get() = _binding!!

    private lateinit var topAppBar: MaterialToolbar
    private val viewModel: MainViewModel by viewModels()
    private val roomsAdapter: RoomsAdapter by lazy { RoomsAdapter() }
    private val args by navArgs<RoomFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRoomBinding.inflate(inflater, container, false)
        navigateBack()
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            if (viewModel.hasInternetConnection()) {
                roomsRecyclerView()
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
            roomsRecyclerView()
            hideErrorMessage()
        } else {
            showErrorMessage()
        }
    }

    private fun roomsRecyclerView() {
        val roomRv = binding.roomRv
        roomRv.adapter = roomsAdapter
        roomRv.layoutManager = LinearLayoutManager(requireContext())
        viewModel.fetchAllRooms(this)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.roomModelList.collectLatest { data ->
                withContext(Dispatchers.Main) {
                    roomsAdapter.differ.submitList(data.rooms)
                    Log.d("RoomFragment", "roomModel: $data")

                }
            }
        }
        roomsAdapter.onRoomClickListener = object : RoomsAdapter.OnBtnRoomClickListener {
            override fun onBtnRoomClick() {
                findNavController().navigate(R.id.action_roomFragment_to_bookingFragment)
            }

        }

    }

    override fun showProgressBar() {
        binding.ProgressBar.visibility = View.VISIBLE
        binding.roomRv.visibility = View.GONE
        super.showProgressBar()
    }

    override fun hideProgressBar() {
        binding.ProgressBar.visibility = View.GONE
        binding.roomRv.visibility = View.VISIBLE
        super.hideProgressBar()
    }

    override fun showError(error: String) {
        binding.roomRv.visibility = View.GONE
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        super.showError(error)
    }

    private fun showErrorMessage() {
        binding.itemErrorMessage.root.visibility = View.VISIBLE
        binding.roomRv.visibility = View.GONE
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.root.visibility = View.GONE
        binding.roomRv.visibility = View.VISIBLE
    }

    private fun navigateBack() {
        topAppBar = binding.topAppBar
        args.hotelName.let { hotel ->
            topAppBar.title = hotel

        }
        topAppBar.setNavigationOnClickListener {
            (requireActivity() as MainActivity).handleBackPress()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}