package com.umsl.gregoryhommert.simon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_results.*

class ResultsActivity : Activity() {

    //MARK:- Keys/Intents
    companion object {
        //Tags
        private const val RESULTS_TAG = "ResultsActivity"

        //Keys
        private const val EXTRA_HIGH_SCORE = "com.umsl.gregoryhommert.simon.high_score"
        private const val EXTRA_DID_WIN = "com.umsl.gregoryhommert.did_win"
        private const  val EXTRA_FINAL_SCORE = "com.umsl.gregoryhommert.final_score"

        //Intent
        fun newIntent(context: Context, finalScore: Int, didWin: Boolean, highScore: Int): Intent {
            val resultsIntent = Intent(context, ResultsActivity::class.java)
            resultsIntent.putExtra(EXTRA_HIGH_SCORE, highScore)
            resultsIntent.putExtra(EXTRA_DID_WIN, didWin)
            resultsIntent.putExtra(EXTRA_FINAL_SCORE, finalScore)
            return resultsIntent
        }
    }

    //MARK:- Activity Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.e("MESSAGE", "$RESULTS_TAG being created.")
        setContentView(R.layout.activity_results)
        setTexts()
        bindButtons()
    }

    override fun onDestroy() {
        super.onDestroy()
        Runtime.getRuntime().gc()
        //Log.e("MESSAGE", "$RESULTS_TAG was destroyed.")
    }

    override fun onBackPressed() {
        buttonPressed()
        super.onBackPressed()
    }

    //MARK:- Setup Functions
    private fun setTexts() {
        val highScoreValue = intent.getIntExtra(EXTRA_HIGH_SCORE, 0)
        val didWin = intent.getBooleanExtra(EXTRA_DID_WIN, false)
        val currentScoreValue = intent.getIntExtra(EXTRA_FINAL_SCORE, 0)

        val currentScoreText = getString(R.string.current_score_text)+" "+currentScoreValue
        val highScoreText = getString(R.string.high_score_text)+" "+highScoreValue

        when(didWin) {
            true -> this.gameOver.text = getString(R.string.you_win_text)
            false -> this.gameOver.text = getString(R.string.game_over_text)
        }

        this.currentScore.text = currentScoreText
        this.highScore.text = highScoreText
    }

    private fun bindButtons() {
        this.newGameButton.setOnClickListener {
            buttonPressed()
        }
    }

    //MARK:- Actions
    private fun buttonPressed() {
        this@ResultsActivity.finish()
    }
}
