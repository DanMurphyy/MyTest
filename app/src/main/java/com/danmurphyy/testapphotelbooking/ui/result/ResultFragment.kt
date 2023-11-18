package com.danmurphyy.testapphotelbooking.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.danmurphyy.testapphotelbooking.R
import com.danmurphyy.testapphotelbooking.databinding.FragmentResultBinding
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var topAppBar: MaterialToolbar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        navigate()
        binding.paidBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_hotelFragment)
        }


        return (binding.root)
    }

    private fun navigate(){
        topAppBar = binding.resultTopAppBar
        topAppBar.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_resultFragment_to_hotelFragment)
        }
    }

}