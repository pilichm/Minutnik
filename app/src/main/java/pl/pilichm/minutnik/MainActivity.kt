package pl.pilichm.minutnik

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mSelectedTime: Int = 0
    private var mOriginalTIme: Int = 0
    private var isCounterRunning = false
    private var mCountDownTimer: CountDownTimer? = null
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpSeekBar()
        setUpButtonListeners()
    }

    private fun setUpSeekBar(){
        selectTimeSeekBar.progress = MAX_TIMER_VALUE_IN_SECONDS/2
        selectTimeSeekBar.max = MAX_TIMER_VALUE_IN_SECONDS
        mSelectedTime = MAX_TIMER_VALUE_IN_SECONDS/2
        mOriginalTIme = MAX_TIMER_VALUE_IN_SECONDS/2

        tvTimeLeft.text = "$mSelectedTime ${resources.getString(R.string.seconds)}"

        selectTimeSeekBar.setOnSeekBarChangeListener(
            object: SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mSelectedTime = progress
                tvTimeLeft.text = "$mSelectedTime ${resources.getString(R.string.seconds)}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setUpButtonListeners(){
        btnStopStart.setOnClickListener {
            if (isCounterRunning){
                btnStopStart.text = resources.getString(R.string.button_start)
                btnStopStart.visibility = View.INVISIBLE
                llResumeRestart.visibility = View.VISIBLE
                mCountDownTimer!!.cancel()
                isCounterRunning = false
                selectTimeSeekBar.isEnabled = true
            } else {
                btnStopStart.text = resources.getString(R.string.button_stop)
                mCountDownTimer = createTimer(mSelectedTime).start()
                isCounterRunning = true
                selectTimeSeekBar.isEnabled = false
            }
        }

        btnResume.setOnClickListener {
            showStopStartButton()
            mCountDownTimer = createTimer(mSelectedTime).start()
        }

        btnRestart.setOnClickListener {
            showStopStartButton()
            mCountDownTimer = createTimer(mOriginalTIme).start()
            mSelectedTime = mOriginalTIme
        }
    }

    private fun showStopStartButton(){
        llResumeRestart.visibility = View.INVISIBLE
        btnStopStart.visibility = View.VISIBLE
        btnStopStart.text = resources.getString(R.string.button_stop)
        isCounterRunning = true
        selectTimeSeekBar.isEnabled = false
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
                tvTimeLeft.text = "$mTImeRemaining ${resources.getString(R.string.seconds)}"
                mMediaPlayer = MediaPlayer.create(applicationContext, R.raw.tick)
                mMediaPlayer!!.start()
            }

            override fun onFinish() {
                isCounterRunning = false
                llResumeRestart.visibility = View.INVISIBLE
                btnStopStart.visibility = View.VISIBLE
                btnStopStart.text = resources.getString(R.string.button_start)
                mSelectedTime = selectTimeSeekBar.progress
                selectTimeSeekBar.isEnabled = true
                mMediaPlayer = MediaPlayer.create(applicationContext, R.raw.bell_two_strikes)
                mMediaPlayer!!.start()
            }
        }
    }

    companion object {
        const val MAX_TIMER_VALUE_IN_SECONDS = 120
        const val TIMER_TICK_PERIOD = 1000L
    }
}