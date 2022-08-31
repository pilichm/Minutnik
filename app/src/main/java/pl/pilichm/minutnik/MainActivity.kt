package pl.pilichm.minutnik

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import pl.pilichm.minutnik.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mSelectedTime: Int = 0
    private var mOriginalTIme: Int = 0
    private var isCounterRunning = false
    private var mCountDownTimer: CountDownTimer? = null
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpSeekBar()
        setUpButtonListeners()
    }

    private fun setUpSeekBar(){
        binding.selectTimeSeekBar.progress = MAX_TIMER_VALUE_IN_SECONDS/2
        binding.selectTimeSeekBar.max = MAX_TIMER_VALUE_IN_SECONDS
        mSelectedTime = MAX_TIMER_VALUE_IN_SECONDS/2
        mOriginalTIme = MAX_TIMER_VALUE_IN_SECONDS/2

        binding.tvTimeLeft.text = "$mSelectedTime ${resources.getString(R.string.seconds)}"

        binding.selectTimeSeekBar.setOnSeekBarChangeListener(
            object: SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mSelectedTime = progress
                binding.tvTimeLeft.text = "$mSelectedTime ${resources.getString(R.string.seconds)}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setUpButtonListeners(){
        binding.btnStopStart.setOnClickListener {
            if (isCounterRunning){
                binding.btnStopStart.text = resources.getString(R.string.button_start)
                binding.btnStopStart.visibility = View.INVISIBLE
                binding.llResumeRestart.visibility = View.VISIBLE
                mCountDownTimer!!.cancel()
                isCounterRunning = false
                binding.selectTimeSeekBar.isEnabled = true
            } else {
                binding.btnStopStart.text = resources.getString(R.string.button_stop)
                mCountDownTimer = createTimer(mSelectedTime).start()
                isCounterRunning = true
                binding.selectTimeSeekBar.isEnabled = false
                binding.ivProgressColor.drawable.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        Color.parseColor("#00FF00"), BlendModeCompat.SRC_ATOP)
            }
        }

        binding.btnResume.setOnClickListener {
            showStopStartButton()
            mCountDownTimer = createTimer(mSelectedTime).start()
        }

        binding.btnRestart.setOnClickListener {
            showStopStartButton()
            mCountDownTimer = createTimer(mOriginalTIme).start()
            mSelectedTime = mOriginalTIme
        }
    }

    private fun showStopStartButton(){
        binding.llResumeRestart.visibility = View.INVISIBLE
        binding.btnStopStart.visibility = View.VISIBLE
        binding.btnStopStart.text = resources.getString(R.string.button_stop)
        isCounterRunning = true
        binding.selectTimeSeekBar.isEnabled = false
        stopSound()
    }

    private fun stopSound(){
        if (mMediaPlayer!=null&&mMediaPlayer!!.isPlaying){
            mMediaPlayer!!.stop()
        }
    }

    private fun createTimer(timePeriod: Int): CountDownTimer {
        return object : CountDownTimer(
            timePeriod * TIMER_TICK_PERIOD, TIMER_TICK_PERIOD) {
            override fun onTick(millisUntilFinished: Long) {
                stopSound()
                mSelectedTime--
                val mTImeRemaining = (millisUntilFinished/ TIMER_TICK_PERIOD).toInt()
                binding.tvTimeLeft.text = "$mTImeRemaining ${resources.getString(R.string.seconds)}"
                mMediaPlayer = MediaPlayer.create(applicationContext, R.raw.tick)
                mMediaPlayer!!.start()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.ivProgressColor.drawable.colorFilter =
                        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            createColorFromTImeLeft(mTImeRemaining).toArgb(), BlendModeCompat.SRC_ATOP)
                }
            }

            override fun onFinish() {
                isCounterRunning = false
                binding.llResumeRestart.visibility = View.INVISIBLE
                binding.btnStopStart.visibility = View.VISIBLE
                binding.btnStopStart.text = resources.getString(R.string.button_start)
                mSelectedTime = binding.selectTimeSeekBar.progress
                binding.selectTimeSeekBar.isEnabled = true
                mMediaPlayer = MediaPlayer.create(applicationContext, R.raw.bell_two_strikes)
                mMediaPlayer!!.start()
            }
        }
    }

    private fun createColorFromTImeLeft(timeLeft: Int): Color {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Color.valueOf(
                ((0.003921569*(timeLeft/100))/255).toFloat(),
                ((255 * (timeLeft/100))/255).toFloat(),
                0f
            )
        } else {
            return Color()
        }
    }

    companion object {
        const val MAX_TIMER_VALUE_IN_SECONDS = 120
        const val TIMER_TICK_PERIOD = 1000L
    }
}