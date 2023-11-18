package com.danmurphyy.testapphotelbooking.ui.room

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.danmurphyy.testapphotelbooking.databinding.ItemHotelRoomBinding
import com.danmurphyy.testapphotelbooking.domain.model.roomM.RoomModel
import com.danmurphyy.testapphotelbooking.utils.priceFormat
import com.denzcoskun.imageslider.models.SlideModel

class RoomsAdapter : RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {

    var onRoomClickListener: OnBtnRoomClickListener? = null

    class ViewHolder(internal val binding: ItemHotelRoomBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHotelRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    private val roomsDiffUtil = object : DiffUtil.ItemCallback<RoomModel>() {
        override fun areItemsTheSame(oldItem: RoomModel, newItem: RoomModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RoomModel, newItem: RoomModel): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, roomsDiffUtil)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val roomModel = differ.currentList[position]
        val binding = holder.binding
        binding.roomName.text = roomModel.name ?: ""
        binding.roomPeculiarities1.text = roomModel.peculiarities.getOrNull(0) ?: ""
        binding.roomPeculiarities2.text = roomModel.peculiarities.getOrNull(1) ?: ""
        binding.roomPrice.text = priceFormat(roomModel.price)
        binding.roomPricePer.text = roomModel.price_per

        val slideModels = roomModel.image_urls.map { url ->
            SlideModel(url)
        }
        binding.roomImageSlider.setImageList(slideModels)

        binding.btnRoom.setOnClickListener {
            onRoomClickListener?.onBtnRoomClick()
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnBtnRoomClickListener {
        fun onBtnRoomClick()
    }

}