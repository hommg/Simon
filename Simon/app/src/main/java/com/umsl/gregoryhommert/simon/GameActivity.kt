package com.umsl.gregoryhommert.simon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : Activity(), SequenceFragment.SequenceFragmentListener,
        TimerFragment.TimerFragmentListener, EndRoundFragment.EndRoundFragmentListener {

    //MARK:- Keys/Intents
    companion object {
        //Tags
        private const val GAME_TAG = "GameActivity"
        private const val SEQ_FRAG_TAG = "GameFragment"
        private const val TIMER_FRAG_TAG = "TimerFragment"
        private const val ENDROUND_FRAG_TAG = "EndRoundFragment"

        //Keys
        private const val EXTRA_DIFFICULTY_SELECTED =
                "com.umsl.gregoryhommert.simon.difficulty_selected"
        private const val EXTRA_HIGH_SCORE = "com.umsl.gregoryhommert.simon.high_score"
        private const val EXTRA_DID_WIN = "com.umsl.gregoryhommert.did_win"
        private const  val EXTRA_FINAL_SCORE = "com.umsl.gregoryhommert.final_score"

        //Intents
        fun highScore(intent: Intent) = intent.getIntExtra(EXTRA_HIGH_SCORE, 0)
        fun didWin(intent: Intent) = intent.getBooleanExtra(EXTRA_DID_WIN, false)
        fun finalScore(intent: Intent) = intent.getIntExtra(EXTRA_FINAL_SCORE, 0)

        fun newIntent(context: Context, difficulty: String?, highScore: Int?): Intent {
            val gameIntent = Intent(context, GameActivity::class.java)
            gameIntent.putExtra(EXTRA_DIFFICULTY_SELECTED, difficulty)
            gameIntent.putExtra(EXTRA_HIGH_SCORE, highScore)
            return gameIntent
        }
    }

    //MARK:- Vars
    private var model: SimonModel? = null
    private var sequenceFragment: SequenceFragment? = null
    private var timerFragment: TimerFragment? = null
    private var endRoundFragment: EndRoundFragment? = null
    private var comparisonPosition: Int = 0

    //MARK:- Activity Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.e("MESSAGE", "$GAME_TAG created.")
        setContentView(R.layout.activity_game)

        //MARK:- Setup Function Calls
        bindButtons()
        prepareModel()
        buildFragments()

        //MARK:- Begin Gameplay
        showSequence()
    }

    override fun onPause() {
        super.onPause()
        setResult(RESULT_CANCELED)
        this@GameActivity.finish()
        //Log.e("MESSAGE", "$GAME_TAG paused.")
    }

    override fun onDestroy() {
        super.onDestroy()
        Runtime.getRuntime().gc()
        //Log.e("MESSAGE", "$GAME_TAG destroyed.")
    }

    //MARK:- Setup Functions
    private fun bindButtons() {
        //MARK:- Backgrounds
        this.greenButton.setBackgroundResource(R.drawable.green_highlight)
        this.redButton.setBackgroundResource(R.drawable.red_highlight)
        this.yellowButton.setBackgroundResource(R.drawable.yellow_highlight)
        this.blueButton.setBackgroundResource(R.drawable.blue_highlight)

        //MARK:- Set OnClickListeners
        this.greenButton.setOnClickListener {
            handleButtonTap(this.greenButton, SimonColor.GREEN)
        }
        this.redButton.setOnClickListener {
            handleButtonTap(this.redButton, SimonColor.RED)
        }
        this.yellowButton.setOnClickListener {
            handleButtonTap(this.yellowButton, SimonColor.YELLOW)
        }
        this.blueButton.setOnClickListener {
            handleButtonTap(this.blueButton, SimonColor.BLUE)
        }
    }

    private fun prepareModel() {
        val difficultyString = intent.getStringExtra(EXTRA_DIFFICULTY_SELECTED)
        val difficulty = getDifficultyLevelFromString(difficultyString)
        val highScore = intent.getIntExtra(EXTRA_HIGH_SCORE, 0)
        model = SimonModel(difficulty, highScore)
    }

    private fun buildFragments() {
        //MARK:- Fragment Manager
        val manager = fragmentManager

        //MARK:- SequenceFragment
        sequenceFragment = manager.findFragmentByTag(SEQ_FRAG_TAG) as? SequenceFragment
        if (sequenceFragment == null) {
            sequenceFragment = SequenceFragment()
            manager.beginTransaction()
                    .add(sequenceFragment, SEQ_FRAG_TAG)
                    .commit()
        }
        sequenceFragment?.listener = this
        sequenceFragment?.delay = model?.getFlashSpeed()

        //MARK:- TimerFragment
        timerFragment = manager.findFragmentByTag(TIMER_FRAG_TAG) as? TimerFragment
        if (timerFragment == null) {
            timerFragment = TimerFragment()
            manager.beginTransaction()
                    .add(timerFragment, TIMER_FRAG_TAG)
                    .commit()
        }
        timerFragment?.listener = this
        timerFragment?.timerLength = model?.getTimeToSelect()

        //MARK:- EndRoundFragment
        endRoundFragment = manager.findFragmentByTag(ENDROUND_FRAG_TAG) as? EndRoundFragment
        if (endRoundFragment == null) {
            endRoundFragment = EndRoundFragment()
            manager.beginTransaction()
                    .add(endRoundFragment, ENDROUND_FRAG_TAG)
                    .commit()
        }
        endRoundFragment?.listener = this
        endRoundFragment?.delay = model?.getFlashSpeed()
    }

    private fun getDifficultyLevelFromString(difficultyString: String?): DifficultyLevel? {
        val level = DifficultyLevel.values().filter { it.name == difficultyString }

        when (level.isNotEmpty()) {
            true -> return level.first()
            false -> return null
        }
    }

    //MARK:- Actions
    private fun handleButtonTap(button: Button, color: SimonColor) {
        flashButton(button)
        if (isValidSelection(color)) {
            playTone(color.rawRes)
            model?.incrementScore()
            progressGamePlay(color)
        } else {
            stopTimer()
            toggleButtons(false)
            val message = getString(R.string.incorrect_selection_text)+"\n"+
                    getString(R.string.current_score_text)+model?.getCurrentScore()+"\n"+
                    getString(R.string.high_score_text)+model?.getHighScore()
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            playTone(R.raw.g2_d2)
            endRoundFragment?.windDown(true)
        }
    }

    private fun flashButton(button: Button) {
        val animation: AnimationDrawable = button.background as AnimationDrawable
        animation.stop()
        animation.selectDrawable(0);
        animation.start()
    }

    private fun flashButtonFromColor(color: SimonColor) {
        when (color) {
            SimonColor.RED -> flashButton(this.redButton)
            SimonColor.GREEN -> flashButton(this.greenButton)
            SimonColor.BLUE -> flashButton(this.blueButton)
            SimonColor.YELLOW -> flashButton(this.yellowButton)
        }
    }

    private fun toggleButtons(state: Boolean) {
        this.greenButton.isEnabled = state
        this.redButton.isEnabled = state
        this.yellowButton.isEnabled = state
        this.blueButton.isEnabled = state
    }

    private fun playTone(rawRes: Int) {
        //SOURCE:- This code was derived from:
        // https://developer.android.com/reference/android/media/MediaPlayer.html
        val tone = MediaPlayer.create(this@GameActivity, rawRes)
        tone.setOnPreparedListener {
            tone.start()
        }
        tone.setOnCompletionListener {
            tone.reset()
        }
    }

    private fun progressGamePlay(color: SimonColor) {
        if (this.comparisonPosition < (model!!.getSequenceSize() - 1)) {
            incrementComparisonPosition()
            resetTimer()
        } else {
            stopTimer()
            if (model!!.colorCanBeAdded()) {
                if(model!!.speedShouldDecrement()) {
                    sequenceFragment?.delay = model?.getFlashSpeed()
                }

                resetComparisonPosition()
                showSequence()
            } else {
                toggleButtons(false)
                val message = getString(R.string.round_completed_text)+"\n"+
                        getString(R.string.current_score_text)+model?.getCurrentScore()+"\n"+
                        getString(R.string.high_score_text)+model?.getHighScore()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                endRoundFragment?.windDown(false)
            }
        }
    }

    //MARK:- Var Mutations
    private fun incrementComparisonPosition() {
        this.comparisonPosition++
    }

    private fun resetComparisonPosition() {
        this.comparisonPosition = 0
    }

    //MARK:- Validation
    private fun isValidSelection(color: SimonColor): Boolean {
        return model!!.validateSelection(this.comparisonPosition, color)
    }

    //MARK:- SequenceFragment Functions
    private fun showSequence() {
        sequenceFragment?.startSequence()
    }

    override fun prepareForSequence() {
        toggleButtons(false)
        val message = getString(R.string.performing_sequence_text)+"\n"+
                getString(R.string.current_score_text)+model?.getCurrentScore()+"\n"+
                getString(R.string.high_score_text)+model?.getHighScore()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun performButtonFlash(index: Int): Boolean {
        if (index < (model!!.getSequenceSize())) {
            val color = model!!.getSequenceElement(index)
            flashButtonFromColor(color)
            playTone(color.rawRes)
            return true
        } else {
            return false
        }
    }

    override fun exitSequence() {
        toggleButtons(true)
        val message = getString(R.string.exit_sequence_text)+"\n"+
                getString(R.string.current_score_text)+model?.getCurrentScore()+"\n"+
                getString(R.string.high_score_text)+model?.getHighScore()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        startTimer()
    }

    //MARK:- TimerFragment Functions
    override fun triggerEndRoundFragment() {
        toggleButtons(false)
        val message = getString(R.string.timer_elapsed_text)+"\n"+
                getString(R.string.current_score_text)+model?.getCurrentScore()+"\n"+
                getString(R.string.high_score_text)+model?.getHighScore()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        playTone(R.raw.g2_d2)
        endRoundFragment?.windDown(true)
    }

    private fun startTimer() {
        timerFragment?.startTimer()
    }

    private fun stopTimer() {
        timerFragment?.stopTimer()
    }

    private fun resetTimer() {
        timerFragment?.resetTimer()
    }

    //MARK:- EndRoundFragment Functions
    override fun showError() {
        var handler: Handler? = Handler()
        val runnable = object : Runnable {
            override fun run() {
                flashButtonFromColor(model!!.getSequenceElement(comparisonPosition))
                handler!!.removeCallbacks(this)
                handler = null
            }
        }

        handler!!.postDelayed(runnable, model!!.getInitialFlashSpeed())
    }

    override fun performVictorySequence() {
        val melody = model?.getVictorySong(model!!.getSequenceElement(comparisonPosition))
        var handler: Handler? = Handler()
        val runnable = object : Runnable {
            override fun run() {
                when (melody!!.size > 0) {
                    true -> {
                        flashButtonFromColor(melody.first())
                        playTone(melody.first().rawRes)
                        melody.removeAt(0)
                        handler!!.postDelayed(this, 100)
                    }
                    false -> {
                        handler!!.removeCallbacks(this)
                        handler = null
                    }
                }
            }
        }

        handler!!.postDelayed(runnable, 100)
    }

    override fun triggerResults(didWin: Boolean) {
        returnResult(didWin)
    }

    private fun returnResult(didWin: Boolean) {
        val data = Intent()
        data.putExtra(EXTRA_HIGH_SCORE, model!!.getHighScore())
        data.putExtra(EXTRA_DID_WIN, didWin)
        data.putExtra(EXTRA_FINAL_SCORE, model!!.getCurrentScore())
        setResult(RESULT_OK, data)
        this@GameActivity.finish()
    }
}
