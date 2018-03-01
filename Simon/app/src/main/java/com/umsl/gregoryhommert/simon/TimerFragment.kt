package com.umsl.gregoryhommert.simon

import android.os.Bundle
import android.app.Fragment
import android.os.CountDownTimer
import android.util.Log

class TimerFragment : Fragment() {

    //MARK:- Keys
    companion object {
        private const val TIMERFRAG_TAG = "TimerFragment"
    }

    //MARK:- Vars
    var listener: TimerFragmentListener? = null
    private var timer: CountDownTimer? = null
    var timerLength: Long? = null


    //MARK:- Listener
    interface TimerFragmentListener {
        fun triggerEndRoundFragment()
    }

    //MARK:- Fragment Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.e("FRAGMENT", "$TIMERFRAG_TAG was created.")
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
        //Log.e("FRAGMENT", "$TIMERFRAG_TAG paused.")
    }

    override fun onDestroy() {
        super.onDestroy()
        //SOURCE:- This line was found on stack
        // https://stackoverflow.com/questions/34409549/
        // release-memory-of-particular-activity-when-it-is-destroyed
        // while researching effective methods for cleaning up resources
        Runtime.getRuntime().gc()
        //Log.e("FRAGMENT", "$TIMERFRAG_TAG was destroyed.")
    }

    //MARK:- Timer Functions
    fun startTimer() {
        this.timer = object : CountDownTimer(timerLength!!, 100) {
            override fun onTick(p0: Long) {
                //NOTE:- Not implemented for scope of this project
            }

            override fun onFinish() {
                //Log.e("FRAGMENT", "$TIMERFRAG_TAG timer finished.")
                listener?.triggerEndRoundFragment()
            }
        }.start()
        //Log.e("FRAGMENT", "$TIMERFRAG_TAG timer started.")
    }

    fun resetTimer() {
        //Log.e("FRAGMENT", "$TIMERFRAG_TAG timer being reset.")
        stopTimer()
        startTimer()
    }

    fun stopTimer() {
        this.timer?.cancel()
        //Log.e("FRAGMENT", "$TIMERFRAG_TAG timer stopped.")
    }
}
