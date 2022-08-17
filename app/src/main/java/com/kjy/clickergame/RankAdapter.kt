package com.kjy.clickergame

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kjy.clickergame.databinding.RankRecyclerBinding

class RankAdapter: RecyclerView.Adapter<CustomHolder>() {

    // 데이터 클래스를 담을 리스트
    var rankData = mutableListOf<InformData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomHolder {
        val binding = RankRecyclerBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return CustomHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomHolder, position: Int) {
        val data = rankData[position]
        holder.setData(data)
    }

    override fun getItemCount(): Int {
        return rankData.size
    }

}

class CustomHolder(val binding: RankRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
    fun setData(informData: InformData) {
        binding.itemUserName.text = informData.userName
        binding.itemSwordNumber.text = "${informData.swordNumber}"
        binding.itemClear.text = informData.clear
    }

}