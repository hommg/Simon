package com.umsl.gregoryhommert.simon

import android.os.Bundle
import android.app.Fragment
import android.os.Handler
import android.util.Log

//SOURCE:- Much of this Fragment's structure was taken from MainFragment.kt
// in 5020 Flashy app
class EndRoundFragment : Fragment() {

    //MARK:- Keys
    companion object {
        private const val ENDROUNDFRAG_TAG = "EndRoundFragment"
    }

    //MARK:- Vars
    private var handler: Handler? = null
    var listener: EndRoundFragmentListener? = null
    var delay: Long? = null
    var showError: Boolean? = null

    //MARK:- Listener
    interface EndRoundFragmentListener {
        fun showError()
        fun performVictorySequence()
        fun triggerResults(didWin: Boolean)
    }

    //MARK:- Fragment Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.e("FRAGMENT", "$ENDROUNDFRAG_TAG was created.")
    }

    override fun onPause() {
        super.onPause()
        releaseHandler()
    }

    override fun onDestroy() {
        super.onDestroy()
        Runtime.getRuntime().gc()
        //Log.e("FRAGMENT", "$ENDROUNDFRAG_TAG was destroyed.")
    }

    //MARK:- EndRound Functions
    fun windDown(_showError: Boolean) {
        this.showError = _showError

        if (handler == null) {
            handler = Handler()
            //Log.e("FRAGMENT", "$ENDROUNDFRAG_TAG handler created.")
            when (showError) {
                true -> {
                    listener?.showError()
                }
                false -> {
                    listener?.performVictorySequence()
                }
            }
            handler?.postDelayed(runnable, (2 * delay!!))
        } else {
            //Log.e("FRAGMENT", "$ENDROUNDFRAG_TAG handler already exists.")
        }
    }

    private fun releaseHandler() {
        handler?.removeCallbacks(runnable)
        handler = null
        //Log.e("FRAGMENT", "$ENDROUNDFRAG_TAG handler set to null.")
    }

    //MARK:- Runnable
    private var runnable = object : Runnable {
        override fun run() {
            listener?.triggerResults(!showError!!)
            releaseHandler()
        }
    }
}
