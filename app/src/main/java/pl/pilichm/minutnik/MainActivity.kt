package pl.pilichm.minutnik

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mSelectedTime: Int = 0
    private var isCounterRunning = false
    private var mCountDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpSeekBar()
        setUpStartStopButton()
    }

    private fun setUpSeekBar(){
        selectTimeSeekBar.progress = MAX_TIMER_VALUE_IN_SECONDS/2
        selectTimeSeekBar.max = MAX_TIMER_VALUE_IN_SECONDS
        mSelectedTime = MAX_TIMER_VALUE_IN_SECONDS/2
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

    private fun setUpStartStopButton(){
        btnStopStart.setOnClickListener {
            if (isCounterRunning){
                btnStopStart.text = resources.getString(R.string.button_start)
                mCountDownTimer!!.cancel()
                isCounterRunning = false
            } else {
                btnStopStart.text = resources.getString(R.string.button_stop)
                mCountDownTimer = object : CountDownTimer(
                    mSelectedTime * TIMER_TICK_PERIOD, TIMER_TICK_PERIOD) {
                    override fun onTick(millisUntilFinished: Long) {
                        val mTImeRemaining = (millisUntilFinished/ TIMER_TICK_PERIOD).toInt()
                        selectTimeSeekBar.progress = mTImeRemaining
                        mSelectedTime = mTImeRemaining
                        tvTimeLeft.text = "${millisUntilFinished/ TIMER_TICK_PERIOD} ${resources.getString(R.string.seconds)}"
                    }

                    override fun onFinish() {
                        isCounterRunning = false
                    }
                }.start()
                isCounterRunning = true
            }
        }
    }

    companion object {
        const val MAX_TIMER_VALUE_IN_SECONDS = 120
        const val TIMER_TICK_PERIOD = 1000L
    }
}