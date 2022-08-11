package com.kjy.clickergame

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kjy.clickergame.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

// View.OnClickListener 리스너 추가
class MainActivity : AppCompatActivity(), View.OnClickListener {

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // 배경음악 설정
    private var mediaPlayer: MediaPlayer? = null


    
    /////// 클릭형 게임에 필요한 변수 설정///////

    // 골드 변수 및 레벨 선언
    var gold: Int = 0
    // 초당 골드 변수
    var goldPerSeconds: Int = 1
    // 클릭당 골드 변수
    var goldPerClick: Int = 1
    // 골드 업그레이드 레벨 배열 설정
    var level: IntArray = IntArray(10)

    // 무기를 알기 위한 리스트
    var swordList = arrayListOf(R.drawable.first_sword, R.drawable.second_sword, R.drawable.sword_3,
                                    R.drawable.sword_4, R.drawable.sword_5, R.drawable.sword_6, R.drawable.sword_7
                                    ,R.drawable.sword_8, R.drawable.sword_9, R.drawable.sword_10)

    // 업그레이드 골드
    var upgradeGold: Int = 30

    // 효과음 설정
    private var soundGoldClick : SoundPool?= null       // 골드 클릭시 효과음

    private var soundUpgrade: SoundPool?= null          // 업그레이드시 효과음


   @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

       // 버튼 클릭 변수 여러개
       binding.upgradeClickGold.setOnClickListener(this)
       binding.upgradePerGold.setOnClickListener(this)
       binding.upgradeSword.setOnClickListener(this)
       binding.gameBackground.setOnClickListener(this)
       binding.buttonClickHundred.setOnClickListener(this)
       binding.buttonPerHundred.setOnClickListener(this)


        // 화면 절전모드 x 항상 유지
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)



       // 업그레이드 시 효과음
       soundUpgrade = SoundPool.Builder().build()
       val upgradeSoundId = soundUpgrade?.load(this, R.raw.coin_sound, 1)


       // 골드 업그레이드 레벨 설정
       for (i in 0..9) {
           level[i] = 1
       }



       // 배경음악 설정
        mediaPlayer = MediaPlayer.create(this, R.raw.maple)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()


        // 메뉴 버튼 클릭시 팝업 메뉴창 열기
        binding.menuBtn.setOnClickListener {
            val popUpMenu = PopupMenu(applicationContext, it)

            menuInflater.inflate(R.menu.game_menu, popUpMenu.menu)
            popUpMenu.show()
            popUpMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    // 배경음악 재생 일시정지
                    R.id.sound_off -> {
                        mediaPlayer?.pause()
                        return@setOnMenuItemClickListener true
                    }

                    // 배경음악 다시 시작
                    R.id.sound_on -> {
                        mediaPlayer?.isLooping = true
                        mediaPlayer?.start()
                        return@setOnMenuItemClickListener true
                    }

                    R.id.home -> {
                        val intent = Intent(this, GameStartActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(intent)
                        finish()
                        return@setOnMenuItemClickListener true
                    } else -> {
                        return@setOnMenuItemClickListener false
                    }

                }
            }
        }


        // 게임 배경 클릭시 효과
      binding.gameBackground.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.lottieCoinAnimation.playAnimation()
                    gold += goldPerClick
                }
            }
            false
        }

        // 타이머 구현해서 초당 골드 수 계산하기
        val timer = Timer()
        timer.schedule(object: TimerTask() {     //     timer 내부는 워커 스레드 이기 때문에 ui 조작 x
            override fun run() {
                gold += goldPerSeconds
                runOnUiThread {                 // Ui 조작
                    displayGold()
                    displayGoldPerClick()
                    displayGoldPerSeconds()
                }
            }
        }, 1000, 1000)
    }


    // 액티비티 종료시 음악도 종료함.
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    // 토탈 골드 텍스트 함수
    private fun displayGold() {
        binding.totalGold.text = "$gold"
    }
    // 클릭당 골드 텍스트 함수
    private fun displayGoldPerClick() {
        binding.touchGold.text = "$goldPerClick"
    }
    // 초당 골드 텍스트 함수
    private fun displayGoldPerSeconds() {
        binding.perGold.text = "$goldPerSeconds"
        }

    override fun onClick(v: View?) {

        when(v?.id) {
            // 게임 배경 클릭시 사운드 효과 넣어줌
            binding.gameBackground.id -> {
                soundGoldClick = SoundPool.Builder().build()
                val goldClickSoundId = soundGoldClick?.load(this, R.raw.coin_sound, 1)
                soundGoldClick?.setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener { soundPool, sampleId, status ->
                    soundGoldClick?.play(goldClickSoundId!!, 1f, 1f, 0, 0, 1f)
                })

                }


            // 골드 업그레이드 버튼 클릭시
            binding.upgradeClickGold.id -> {
                Log.d("Click Gold Button", "클릭당 골드 증가 버튼 클릭하였습니다.")
                if(gold >= level[0] * upgradeGold) {
                    gold -= level[0] * upgradeGold
                    goldPerClick += 5
                    level[0] ++
                    displayGold()
                    displayGoldPerClick()
                    displayUpgradeClick()
                }else {
                    Toast.makeText(this, "골드가 부족합니다.", Toast.LENGTH_SHORT).show()
                }

            }

            // 초당 골드 업그레이드 버튼 클릭시
            binding.upgradePerGold.id -> {
                Log.d("Per Gold Button", "초당 골드 증가 버튼 클릭하였습니다.")
                if (gold >= level[1] * upgradeGold) {
                    gold -= level[1] * upgradeGold
                    goldPerSeconds += 3
                    level[1] ++
                    displayGold()
                    displayGoldPerSeconds()
                    displayUpgradeSeconds()
                }else {
                    Toast.makeText(this, "골드가 부족합니다.", Toast.LENGTH_SHORT).show()
                }

            }

            // 클릭당 100배 업그레이드
            binding.buttonClickHundred.id -> {
                Log.d("Hundred click Button", "클릭당 100배 버튼 클릭하였습니다.")
                gold += goldPerClick * 100
                displayGold()
            }

            // 초당 100배 업그레이드
            binding.buttonPerHundred.id -> {
                Log.d("Hundred per Button", "초당 100배 버튼 클릭하였습니다.")
                gold += goldPerSeconds * 100
            }

            // 무기 구매
            binding.upgradeSword.id -> {
                Log.d("Upgrade Sword", "무기 업그레이드 버튼 클릭하였습니다.")

                when (gold) {
                    in 1001..4999 -> {
                        gold -= 1000
                        binding.swordImage.setImageResource(swordList[1])
                    }
                    in 5000..9999 -> {
                        gold -= 5000
                        binding.swordImage.setImageResource(swordList[2])
                    }
                    in 10000..19999 -> {
                        gold -= 10000
                        binding.swordImage.setImageResource(swordList[3])
                    }
                    in 20000..49999 -> {
                        gold -= 20000
                        binding.swordImage.setImageResource(swordList[4])
                    }
                    in 50000..99999 -> {
                        gold -= 50000
                        binding.swordImage.setImageResource(swordList[5])
                    }
                    in 100000..999999 -> {
                        gold -= 100000
                        binding.swordImage.setImageResource(swordList[6])
                    }
                    in 1000000..4999999 -> {
                        gold -= 5000000
                        binding.swordImage.setImageResource(swordList[7])
                    }
                    in 5000000..9999999 -> {
                        gold -= 10000000
                        binding.swordImage.setImageResource(swordList[8])
                    }
                    in 10000000..99999999 -> {
                        gold -= 100000000
                        binding.swordImage.setImageResource(swordList[9])
                    }
                    else -> {
                        Toast.makeText(this, "돈을 더 모아오너라", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            }
        }

    private fun displayUpgradeClick() {
        binding.upgradeClickGoldText.text = "${level[0] * upgradeGold}"
        }

    private fun displayUpgradeSeconds() {
        binding.upgradePerGoldText.text = "${level[1] * upgradeGold}"
    }

    }



