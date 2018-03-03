package com.umsl.gregoryhommert.simon

import android.os.Bundle
import android.app.Fragment
import android.os.Handler
import android.util.Log

//SOURCE:- Much of this Fragment's structure was taken from MainFragment.kt
// in 5020 Flashy app
class SequenceFragment : Fragment() {

    //MARK:- Keys
    companion object {
        private const val SEQFRAG_TAG = "SequenceFragment"
    }

    //MARK:- Vars
    private var handler: Handler? = null
    var listener: SequenceFragmentListener? = null
    var delay: Long? = null
    private var sequenceIndex: Int? = null

    //MARK:- Listener
    interface SequenceFragmentListener {
        fun prepareForSequence()
        fun performButtonFlash(index: Int): Boolean
        fun exitSequence()
    }

    //MARK:- Fragment Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.e("FRAGMENT", "$SEQFRAG_TAG was created.")
    }

    override fun onPause() {
        super.onPause()
        stopSequence()
    }

    override fun onDestroy() {
        super.onDestroy()
        Runtime.getRuntime().gc()
        //Log.e("FRAGMENT", "$SEQFRAG_TAG was destroyed.")
    }

    //MARK:- Var Mutations
    private fun incrementSequenceIndex() {
        when (this.sequenceIndex != null) {
            true -> this.sequenceIndex = this.sequenceIndex!! + 1
            else -> {
                Log.e("FRAGMENT", "$SEQFRAG_TAG: ERROR!!! Found null while" +
                        "unwrapping sequenceIndex (${this.sequenceIndex}).")
            }
        }
    }

    //MARK:- Sequence Functions
    fun startSequence() {
        listener?.prepareForSequence()
        this.sequenceIndex = 0
        if (handler == null) {
            handler = Handler()
            //Log.e("FRAGMENT", "$SEQFRAG_TAG handler created.")
            handler?.postDelayed(runnable, delay!!)
        } else {
            //Log.e("FRAGMENT", "$SEQFRAG_TAG handler already exists.")
        }
    }

    fun stopSequence() {
        handler?.removeCallbacks(runnable)
        handler = null
        //Log.e("FRAGMENT", "$SEQFRAG_TAG handler set to null.")
    }

    //MARK:- Runnable
    private var runnable = object : Runnable {
        override fun run() {
            when (listener!!.performButtonFlash(sequenceIndex!!)) {
                true -> {
                    incrementSequenceIndex()
                    handler?.postDelayed(this, delay!!)
                }
                false -> {
                    listener?.exitSequence()
                    stopSequence()
                }
            }
        }
    }
}
