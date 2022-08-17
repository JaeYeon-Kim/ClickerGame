package com.kjy.clickergame

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kjy.clickergame.databinding.ActivityRankingBinding


// 랭킹을 관리하는 액티비티
// sharedPreferences에 저장된 것을 가져올것임
class RankingActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityRankingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // SharedPreferences 불러오기
        val preferences = getSharedPreferences("RankData", Context.MODE_PRIVATE)
        val jsonData = preferences.getString("rank", "")

        val gson = Gson()
        val token: TypeToken<MutableList<InformData>> = object : TypeToken<MutableList<InformData>>(){}
        val list: MutableList<InformData>? = gson.fromJson(jsonData, token.type)


        // 어댑터 변수 선언
        var adapter = RankAdapter()
        adapter.rankData = list!!

        // 어댑터 연결
        binding.rankRv.adapter = adapter

        binding.rankRv.layoutManager = LinearLayoutManager(this)

    }
}