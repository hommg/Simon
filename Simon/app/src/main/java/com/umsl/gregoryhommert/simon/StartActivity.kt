package com.umsl.gregoryhommert.simon

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : Activity(), AdapterView.OnItemSelectedListener  {

    //MARK:- Keys
    companion object {
        private const val START_TAG = "StartActivity"
        private const val REQUEST_CODE_GAME = 0
        private const val REQUEST_CODE_HIGH_SCORES = 2

        private val REQUEST_CODE_PERMISSIONS = 1
        private val PERMISSIONS: Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    //MARK:- Vars
    private var difficultySelected: String? = null
    private var highScore: Int? = null
    private var highScoresModel: HighScoresModel? = null

    //MARK:- Activity Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.e("MESSAGE", "$START_TAG being created.")
        setContentView(R.layout.activity_start)

        //MARK:- Setup Function Calls
        verifyPermissions()
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
                val didWin = GameActivity.didWin(data)
                val finalScore = GameActivity.finalScore(data)

                if (this.highScoresModel!!.addIfPossible(finalScore)) {
                    this.highScoresModel!!.persist()
                    this.highScore = this.highScoresModel!!.getHighestScore()
                }

                val intent = ResultsActivity.newIntent(this@StartActivity,
                        finalScore, didWin, this.highScore!!)
                startActivity(intent)
            }
        }

        if (requestCode == REQUEST_CODE_HIGH_SCORES) {
            data?.let {
                val scoresWereCleared = HighScoresActivity.scoresWereCleared(data)

                if (scoresWereCleared) {
                    this.highScoresModel!!.clearHighScores()
                    this.highScoresModel!!.persist()
                    this.highScore = 0
                }
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
            //MARK:- Create Intent/ StartActivityForResult
            val intent = GameActivity.newIntent(this@StartActivity,
                    this.difficultySelected, this.highScore)
            startActivityForResult(intent, REQUEST_CODE_GAME)
        }
        this.viewHighScoresButton.setOnClickListener {
            //MARK:- Create Intent/ StartActivityForResult
            val intent = HighScoresActivity.newIntent(this@StartActivity,
                    this.highScoresModel!!.getHighScores())
            startActivityForResult(intent, REQUEST_CODE_HIGH_SCORES)
        }
    }

    private fun populateHighScores() {
        this.highScoresModel = HighScoresModel("high_scores.json",
                getString(R.string.package_name))
        this.highScoresModel!!.populate()

        if (this.highScoresModel!!.getHighScores().isNotEmpty()) {
            this.highScore = this.highScoresModel!!.getHighestScore()
        }
    }

    //MARK:- AdapterView.OnItemSelectedListener Functions
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        this.difficultySelected = p0!!.getItemAtPosition(p2) as? String
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        //NOTE:- Not implemented for scope of this project
    }

    //MARK:- Read/Write Permissions
    private fun verifyPermissions() {
        val permissionWrite = ActivityCompat.checkSelfPermission(this, PERMISSIONS[1])

        when (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            true -> ActivityCompat.requestPermissions(this,
                    PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            else -> populateHighScores()
        }
    }

    //SOURCE:- https://stackoverflow.com/questions/33162152/storage-permission-error-in-marshmallow
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            true -> populateHighScores()
            else -> {
                Log.e("MESSAGE", "ERROR granting permissions.")
            }
        }
    }
}
