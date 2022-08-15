package com.kjy.clickergame

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.kjy.clickergame.databinding.ActivityBossBinding
import java.util.*
import kotlin.concurrent.timer

class BossActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityBossBinding.inflate(layoutInflater)
    }

    // 보스전에서 이전의 칼 정보를 알기 위함
    var swordNumber: Int = 1

    // 보스전에서 칼의 종류에 따른 데미지
    var swordDamage: Int = 1


    // 보스 스테이지의 액티비티의 배경음악을 재생시키기 위한 액티비티
    private var mediaPlayer: MediaPlayer?= null

    // 타이머 구현
    private var time = 0
    private var timerTask: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 화면 절전모드 x 항상 유지
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        // 배경음악 시작
        mediaPlayer = MediaPlayer.create(this, R.raw.boss_music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        bossTime()

        // gif 사용을 위해 glide 라이브러리 사용
        Glide.with(this)
            .load(R.raw.moving_boss)
            .into(binding.bossImage)

        // MainActivity로부터 현재 검의 정보를 가져옴
        val intentNumber = intent.getStringExtra("swordNumber")?.toInt()

        Log.d("intentSword", "$intentNumber")

        // 가져온 검의 정보에 따라 검의 이미지를 변경 시켜줌
        when (intentNumber) {
            1 -> {
                binding.mainSword.setImageResource(R.drawable.first_sword)
                swordNumber++
                swordDamage = 10
            }
            2 -> {
                binding.mainSword.setImageResource(R.drawable.second_sword)
                swordNumber++
                swordDamage = 50
            }

            3 -> {
                binding.mainSword.setImageResource(R.drawable.sword_3)
                swordNumber++
                swordDamage = 100
            }

            4 -> {
                binding.mainSword.setImageResource(R.drawable.sword_4)
                swordNumber++
                swordDamage = 300
            }

            5 -> {
                binding.mainSword.setImageResource(R.drawable.sword_5)
                swordNumber++
                swordDamage = 500
            }

            6 -> {
                binding.mainSword.setImageResource(R.drawable.sword_6)
                swordNumber++
                swordDamage = 1000
            }

            7 -> {
                binding.mainSword.setImageResource(R.drawable.sword_7)
                swordNumber++
                swordDamage = 3000
            }
            8 -> {
                binding.mainSword.setImageResource(R.drawable.sword_8)
                swordNumber++
                swordDamage = 5000
            }

            9 -> {
                binding.mainSword.setImageResource(R.drawable.sword_9)
                swordNumber++
                swordDamage = 10000
            }

            10 -> {
                binding.mainSword.setImageResource(R.drawable.sword_10)
                swordDamage = 20000
            }
            else -> {
                binding.mainSword.setImageResource(R.drawable.first_sword)
            }
        }

        // 보스의 토벌시간 30초 동안 얼마정도의 Hp를 깎았는지 1초마다 표시해주기위한 프로그래스바의 타이머
        // 이전 액티비티에서 보스 액티비티로 전환시 바로 시작!
        val progressCountDown = object: CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // 총 30초 1초의 interval 마다 ui 업데이트
                // 무기 종류에 따라 초당 데미지가 다르게 들어감
                when(swordNumber) {
                    1 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }
                    2 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }

                    3 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }

                    4 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }

                    5 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }

                    6 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }

                    7 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }

                    8 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }

                    9 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }

                    10 -> {
                        binding.bossHp.incrementProgressBy(-swordDamage)
                    }
                }
            }

            override fun onFinish() {
                timerTask?.cancel()

            }

        }
        progressCountDown.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    @SuppressLint("SetTextI18n")
    private fun bossTime() {
        timerTask = timer (period = 10) {
            time ++

            val sec = time / 100
            val milli = time % 100

            // ui 스레드 기능
            runOnUiThread {
                binding.secTimer.text = "${sec}"
                binding.milliTimer.text = "${milli}"

            }
        }
    }



}