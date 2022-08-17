package com.kjy.clickergame

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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
import kotlin.concurrent.timer

// View.OnClickListener 리스너 추가
class MainActivity : AppCompatActivity(), View.OnClickListener {

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    // 클릭당 골드 t/f 파악을 위한 변수 설정
    private var hundredClick = false

    // 초당 골드 t/f 파악을 위한 변수 설정
    private var hundredSeconds = false


    // 배경음악 설정
    private var mediaPlayer: MediaPlayer? = null

    // 검의 이미지 파악을 위한 번호 매기기 변수
    private var swordNumber: Int = 1




    /////// 클릭형 게임에 필요한 변수 설정///////

    // 골드 변수 및 레벨 선언
    var gold: Int = 0
    // 초당 골드 변수
    var goldPerSeconds: Int = 1
    // 클릭당 골드 변수
    var goldPerClick: Int = 1
    // 골드 업그레이드 레벨 배열 설정
    var level: IntArray = IntArray(50)

    // 업그레이드 골드
    var upgradeGold: Int = 30

    // 효과음 설정
    private var soundGoldClick : SoundPool?= null       // 골드 클릭시 효과음

    private var soundUpgrade: SoundPool?= null          // 업그레이드시 효과음


   @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


       // 보스 시작
       bossStart()

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
       for (i in 0..49) {
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
                    }
                    else -> {
                        return@setOnMenuItemClickListener false
                    }

                }
            }
        }


        // 게임 배경 클릭시 기본 돈이 올라감 (기본 클리커)
      binding.gameBackground.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.lottieCoinAnimation.playAnimation()
                    when(hundredClick) {
                        false -> {
                            gold += goldPerClick
                        }
                        true -> {
                            gold += goldPerClick * 100
                        }
                    }
                }
            }
            false
        }

       // 저장한 것을 불러오는 함수
       loaded()





        // 타이머 구현해서 초당 골드 수 계산하기
        val timer = Timer()
        timer.schedule(object: TimerTask() {     //     timer 내부는 워커 스레드 이기 때문에 ui 조작 x
            override fun run() {
                when (hundredSeconds) {
                    false -> {
                        gold += goldPerSeconds
                    }
                    true -> {
                        gold += goldPerSeconds * 100
                    }
                }

                runOnUiThread {                 // Ui 조작
                    displayGold()
                    displayGoldPerClick()
                    displayGoldPerSeconds()
                }
            }
        }, 1000, 1000)

       // 타이머 설정하여 게임 실행 5초 이후에 10초마다 자동 저장
       val saveTimer = Timer()
       saveTimer.schedule(object: TimerTask() {
           override fun run() {
               saved()
           }

       }, 5000, 1000)
    }


    // 타이머 진행중 다른 액티비티로 전환시 호출되어 타이머를 종료 시켜줌.
    // 데이터 저장이나 스레드 중지를 처리하기 좋은 메소드
    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        clickCountDown.cancel()         // 클릭당 100배 타이머 정지
        secondCountDown.cancel()        // 초당 100배 타이머 정지

    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
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

    // 클릭당 골드 t/f 파악을 위한 카운트다운 타이머 만들기
    private val clickCountDown: CountDownTimer = object: CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            hundredClick = true
        }

        override fun onFinish() {
            hundredClick = false
        }

    }

    // 초당 골드 t/f 파악을 위한 카운트다운 타이머 만들기
    private val secondCountDown: CountDownTimer = object: CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            hundredSeconds = true
        }

        override fun onFinish() {
            hundredSeconds = false
        }

    }

    override fun onBackPressed() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("게임 종료")
        builder.setMessage("정말 게임을 종료하시겠습니까?")
        var listener = object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, p1: Int) {
                when (p1) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        saved()
                        finish()
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog?.cancel()
                    }
                }
            }
        }
        builder.setPositiveButton("예", listener)
        builder.setNegativeButton("아니오", listener)
        builder.show()
    }


    override fun onClick(v: View?) {

        when(v?.id) {
            // 게임 배경 클릭시 사운드 효과 넣어줌
            binding.gameBackground.id -> {
                soundGoldClick = SoundPool.Builder().build()
                val goldClickSoundId = soundGoldClick?.load(this, R.raw.coin_sound, 1)
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(object: Runnable {
                    override fun run() {
                        soundGoldClick?.play(goldClickSoundId!!, 1f, 1f, 0, 0, 1f)
                    }

                }, 100)


                Log.d("무기 레벨", "$swordNumber")

                }


            // 골드 업그레이드 버튼 클릭시
            binding.upgradeClickGold.id -> {
                Log.d("Click Gold Button", "클릭당 골드 증가 버튼 클릭하였습니다.")
                if(gold >= level[0] * upgradeGold) {
                    gold -= level[0] * upgradeGold
                    goldPerClick += 5
                    level[0] ++
                    Log.d("업그레이드 현재 수치", "${level[0]}")
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
                if (gold >= level[1] * upgradeGold) {               // level[0] = 1
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
                clickCountDown.start()

            }

            // 초당 100배 업그레이드
            binding.buttonPerHundred.id -> {
                Log.d("Hundred per Button", "초당 100배 버튼 클릭하였습니다.")
                secondCountDown.start()

            }

            // 무기 구매
            binding.upgradeSword.id -> {
                Log.d("Upgrade Sword", "무기 업그레이드 버튼 클릭하였습니다")
                when(swordNumber) {
                        1 -> {
                            if (gold > 1000) {
                                gold -= 1000
                                binding.swordImage.setImageResource(R.drawable.second_sword)
                                swordNumber = 2
                        }
                     }
                        2 -> {
                             if (gold > 5000){
                                 gold -= 5000
                                binding.swordImage.setImageResource(R.drawable.sword_3)
                                swordNumber = 3
                        }
                        }

                        3 -> {
                            if (gold > 10000) {
                                gold -= 10000
                                binding.swordImage.setImageResource(R.drawable.sword_4)
                                swordNumber = 4
                            }
                        }

                        4 -> {
                            if (gold > 50000) {
                                gold -= 50000
                                binding.swordImage.setImageResource(R.drawable.sword_5)
                                swordNumber = 5
                            }
                        }
                        5 -> {
                            if (gold > 100000) {
                                gold -= 100000
                                binding.swordImage.setImageResource(R.drawable.sword_6)
                                swordNumber = 6

                            }
                        }
                        6 -> {
                            if (gold > 1000000) {
                                gold -= 1000000
                                binding.swordImage.setImageResource(R.drawable.sword_7)
                                swordNumber = 7
                            }
                        }
                        7 -> {
                            if (gold > 5000000) {
                                gold -= 5000000
                                binding.swordImage.setImageResource(R.drawable.sword_8)
                                swordNumber = 8
                            }
                        }
                        8 -> {
                            if (gold > 10000000) {
                                gold -= 10000000
                                binding.swordImage.setImageResource(R.drawable.sword_9)
                                swordNumber = 9
                            }
                        }
                        9 -> {
                            if (gold > 50000000) {
                                gold -= 50000000
                                binding.swordImage.setImageResource(R.drawable.sword_10)
                            }
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


    // 보스 시작 버튼
    // 현재 검의 정보를 넘겨주어야 한다.
    // int형의 swordNumber
    private fun bossStart() {
        binding.bossStartBtn.setOnClickListener{
            val intent = Intent(this, BossActivity::class.java)
            intent.putExtra("swordNumber", swordNumber.toString())
            Log.d("swordNumber", "$swordNumber")
            startActivity(intent)
        }
    }

    // 저장 & 불러오기 기능 구현하기
    // sharedPreferences에 저장해줌
    private fun saved() {
        val shared = getSharedPreferences("ClickMain", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putInt("Gold", gold)
        editor.putInt("GoldPerClick", goldPerClick)
        editor.putInt("GoldPerSeconds", goldPerSeconds)
        editor.putInt("SwordNumber", swordNumber)
        editor.putInt("level0", level[0])
        editor.putInt("level1", level[1])
        editor.apply()          // apply를 해주어야 sharedPreferences에 저장이 된다.
    }

    // sharedPreferences에 저장된 정보 불러오기
    private fun loaded() {
        val shared = getSharedPreferences("ClickMain", Context.MODE_PRIVATE)
        gold = shared.getInt("Gold", 1)
        goldPerClick = shared.getInt("GoldPerClick", 1)
        goldPerSeconds = shared.getInt("GoldPerSeconds", 1)
        swordNumber = shared.getInt("SwordNumber", 1)
        level[0] = shared.getInt("level0", 1)
        level[1] = shared.getInt("level1", 1)

    }


    // 초기화 하는 함수
    private fun reset() {
        val shared = getSharedPreferences("ClickMain", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.clear()
        editor.apply()
    }


    }



