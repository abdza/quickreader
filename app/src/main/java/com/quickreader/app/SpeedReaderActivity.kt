package com.quickreader.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.quickreader.app.databinding.ActivitySpeedReaderBinding

class SpeedReaderActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySpeedReaderBinding
    private lateinit var words: List<String>
    private var currentWordIndex = 0
    private var isPlaying = false
    private var readingSpeed = 250
    private val handler = Handler(Looper.getMainLooper())
    private var readingRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeedReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = intent.getStringExtra("text") ?: ""
        readingSpeed = intent.getIntExtra("speed", 250)
        
        words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }
        
        setupUI()
        setupControls()
    }

    private fun setupUI() {
        binding.currentSpeedValue.text = readingSpeed.toString()
        binding.speedAdjustSeekBar.progress = readingSpeed - 50
        updateProgress()
        
        if (words.isNotEmpty()) {
            binding.wordDisplay.text = words[0]
        }
    }

    private fun setupControls() {
        binding.playPauseButton.setOnClickListener {
            if (isPlaying) {
                pauseReading()
            } else {
                startReading()
            }
        }

        binding.stopButton.setOnClickListener {
            stopReading()
        }

        binding.speedAdjustSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    readingSpeed = progress + 50
                    binding.currentSpeedValue.text = readingSpeed.toString()
                    
                    if (isPlaying) {
                        stopCurrentReading()
                        scheduleNextWord()
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.readingProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val wasPlaying = isPlaying
                    if (isPlaying) {
                        pauseReading()
                    }
                    
                    currentWordIndex = (progress - 1).coerceIn(0, words.size - 1)
                    if (words.isNotEmpty()) {
                        binding.wordDisplay.text = words[currentWordIndex]
                    }
                    updateProgress()
                    
                    if (wasPlaying) {
                        startReading()
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (isPlaying) {
                    pauseReading()
                }
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.nextPageButton.setOnClickListener {
            finish()
        }
    }

    private fun startReading() {
        if (currentWordIndex >= words.size) {
            currentWordIndex = 0
        }
        
        isPlaying = true
        binding.playPauseButton.text = "⏸"
        scheduleNextWord()
    }

    private fun pauseReading() {
        isPlaying = false
        binding.playPauseButton.text = "▶"
        stopCurrentReading()
    }

    private fun stopReading() {
        isPlaying = false
        currentWordIndex = 0
        binding.playPauseButton.text = "▶"
        stopCurrentReading()
        
        if (words.isNotEmpty()) {
            binding.wordDisplay.text = words[0]
        }
        updateProgress()
    }

    private fun stopCurrentReading() {
        readingRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun scheduleNextWord() {
        if (currentWordIndex < words.size && isPlaying) {
            binding.wordDisplay.text = words[currentWordIndex]
            updateProgress()
            
            currentWordIndex++
            
            if (currentWordIndex < words.size) {
                val delayMs = (60000 / readingSpeed).toLong()
                
                readingRunnable = Runnable {
                    scheduleNextWord()
                }
                handler.postDelayed(readingRunnable!!, delayMs)
            } else {
                isPlaying = false
                binding.playPauseButton.text = "▶"
                binding.wordDisplay.text = getString(R.string.reading_complete)
            }
        }
    }

    private fun updateProgress() {
        binding.progressText.text = "${currentWordIndex + 1} / ${words.size} words"
        binding.readingProgress.max = words.size
        binding.readingProgress.progress = currentWordIndex + 1
    }

    private fun jumpToWord(index: Int) {
        currentWordIndex = index.coerceIn(0, words.size - 1)
        if (words.isNotEmpty()) {
            binding.wordDisplay.text = words[currentWordIndex]
        }
        updateProgress()
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pauseReading()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCurrentReading()
    }
}