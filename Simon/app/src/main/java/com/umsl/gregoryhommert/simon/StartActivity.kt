package com.umsl.gregoryhommert.simon

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : Activity(), AdapterView.OnItemSelectedListener  {

    //MARK:- Keys
    companion object {
        private const val START_TAG = "StartActivity"
        private const val REQUEST_CODE_GAME = 0
    }

    //MARK:- Vars
    private var difficultySelected: String? = null
    private var highScore: Int? = null

    //MARK:- Activity Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.e("MESSAGE", "$START_TAG being created.")
        setContentView(R.layout.activity_start)

        //MARK:- Setup Function Calls
        setupSpinner()
        bindButtons()
    }

    override fun onDestroy() {
        super.onDestroy()
        //SOURCE:- This line was found on stack
        // https://stackoverflow.com/questions/34409549/
        // release-memory-of-particular-activity-when-it-is-destroyed
        // while researching effective methods for cleaning up resources
        Runtime.getRuntime().gc()
        //Log.e("MESSAGE", "$START_TAG was destroyed.")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_GAME) {
            data?.let {
                highScore = GameActivity.highScore(data)
                val didWin = GameActivity.didWin(data)
                val finalScore = GameActivity.finalScore(data)

                val intent = ResultsActivity.newIntent(this@StartActivity,
                        finalScore, didWin, this.highScore!!)
                startActivity(intent)
            }
        }
    }

    //MARK:- Setup Functions
    private fun setupSpinner() {
        //MARK:- Link Spinner/Context to onItemSelectedListener
        this.difficultyChoices.onItemSelectedListener = this
        //MARK:- Create String Array from @StringRes string-array, and set DropDownViewResource
        val arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.difficultyOptions_array, R.layout.spinner_item)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        //MARK:- Link Newly Created Array to Spinner
        this.difficultyChoices.adapter = arrayAdapter
    }

    private fun bindButtons() {
        this.startButton.setOnClickListener {
            //Mark:- Create Intent/ StartActivityForResult
            val intent = GameActivity.newIntent(this@StartActivity,
                    this.difficultySelected, this.highScore)
            startActivityForResult(intent, REQUEST_CODE_GAME)
        }
    }

    //MARK:- AdapterView.OnItemSelectedListener Functions
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        this.difficultySelected = p0!!.getItemAtPosition(p2) as? String
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        //NOTE:- Not implemented for scope of this project
    }
}
