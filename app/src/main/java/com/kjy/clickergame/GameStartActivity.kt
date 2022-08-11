package com.kjy.clickergame

import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.kjy.clickergame.databinding.ActivityGameStartBinding
import com.kjy.clickergame.databinding.ActivityMainBinding

class GameStartActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityGameStartBinding.inflate(layoutInflater)
    }

    private var mediaPlayer: MediaPlayer? = null

    private var startSound: SoundPool?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 화면 항상 유지
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 배경음악 설정
        mediaPlayer = MediaPlayer.create(this, R.raw.start_game_sound)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()


            binding.gameStartBtn.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}