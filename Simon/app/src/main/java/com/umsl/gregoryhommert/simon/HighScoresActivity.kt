package com.umsl.gregoryhommert.simon

import android.app.Activity
import android.content.Context
import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_high_scores.*

class HighScoresActivity : Activity() {

    //MARK:- Keys/Intents
    companion object {
        //Tags
        private const val HIGH_SCORES_TAG = "HighScoresActivity"

        //Keys
        private const val EXTRA_HIGH_SCORES = "com.umsl.gregoryhommert.simon.high_scores"
        private const val EXTRA_SCORES_CLEARED = "com.umsl.gregoryhommert.scores_cleared"

        //Constants
        private const val SIZE = 10

        //Intents
        fun scoresWereCleared(intent: Intent) = intent.getBooleanExtra(
                EXTRA_SCORES_CLEARED, false)

        fun newIntent(context: Context, highScores: ArrayList<Int>): Intent {
            val highScoresIntent = Intent(context, HighScoresActivity::class.java)
            highScoresIntent.putIntegerArrayListExtra(EXTRA_HIGH_SCORES, highScores)
            return highScoresIntent
        }
    }

    //MARK:- Vars
    private var highScoresToString: ArrayList<String>? = null
    private var clearScoresPressed: Boolean = false
    private var arrayAdapter: ArrayAdapter<String>? = null

    //MARK:- Activity Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)
        //Log.e("MESSAGE", "$HIGH_SCORES_TAG being created.")

        //MARK:- Setup Function Calls
        prepareTableDataSource()
        setupTable()
        bindButtons()
    }

    override fun onDestroy() {
        super.onDestroy()
        Runtime.getRuntime().gc()
        //Log.e("MESSAGE", "$HIGH_SCORES_TAG was destroyed.")
    }

    override fun onBackPressed() {
        setResultForActivity()
        super.onBackPressed()
    }

    //MARK:- Setup Functions
    private fun prepareTableDataSource() {
        this.highScoresToString = ArrayList<String>()
        val scores = intent.getIntegerArrayListExtra(EXTRA_HIGH_SCORES)

        when (scores.isEmpty()) {
            true -> {
                this.clearScoresButton.isEnabled = false
                this.clearScoresButton.visibility = View.GONE

                for (value in 0 until SIZE) {
                    this.highScoresToString!!.add("${value + 1}. "
                            +getString(R.string.empty_score_text))
                }
            }
            else -> {
                if ((scores.filter { it == 0 }).size == scores.size) {
                    this.clearScoresButton.isEnabled = false
                    this.clearScoresButton.visibility = View.GONE
                }

                for (index in 0 until scores.size) {
                    when (scores.elementAt(index) == 0) {
                        true -> this.highScoresToString!!.add("${index + 1}. "
                                +getString(R.string.empty_score_text))
                        else -> this.highScoresToString!!.add("${index + 1}. "
                                +scores.elementAt(index).toString())
                    }
                }

                val remainder = (SIZE - scores.size)

                if(remainder > 0) {
                    for (value in 0 until remainder) {
                        this.highScoresToString!!.add("${value + 1 + scores.size}. "
                                +getString(R.string.empty_score_text))
                    }
                }
            }
        }
    }

    private fun setupTable() {
        this.arrayAdapter = ArrayAdapter(this,
                R.layout.simple_list_simon, this.highScoresToString)
        this.scoresListView.adapter = arrayAdapter
    }

    private fun bindButtons() {
        this.clearScoresButton.setOnClickListener {
            clearScoresButtonPressed()
        }
    }

    //MARK:- Actions
    private fun clearScoresButtonPressed() {
        this.clearScoresPressed = true
        this.highScoresToString!!.clear()
        setResultForActivity()
        /*
        for (index in 0 until SIZE) {
            this.highScoresToString!!.add("${index + 1}. "
                    +getString(R.string.empty_score_text))
        }

        updateUI()
        */
    }

    /*
    private fun updateUI() {
        this.arrayAdapter!!.notifyDataSetChanged()
    }
    */

    //MARK:- Set Result
    private fun setResultForActivity() {
        val data = Intent()
        data.putExtra(EXTRA_SCORES_CLEARED, this.clearScoresPressed)
        setResult(RESULT_OK, data)
        this@HighScoresActivity.finish()
    }
}
