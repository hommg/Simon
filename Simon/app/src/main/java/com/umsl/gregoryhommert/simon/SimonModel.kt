package com.umsl.gregoryhommert.simon

import java.util.*
import kotlin.collections.ArrayList
import android.util.Log

//Log.i("INFO", "")

class SimonModel(_difficulty: DifficultyLevel? = DifficultyLevel.EASY, _highScore: Int) {

    //MARK:- Vars
    private val sequence: ArrayList<SimonColor> //NOTE:- ArrayList containing sequence of color values
    private var difficulty: DifficultyLevel
    private var highScore: Int
    private var currentScore: Int
    private var timeToSelect: Long              //NOTE:- Constant in MB's Simon
    private var flashSpeed: Long
    private var decrementList: ArrayList<Int>

    //MARK:- Init
    init {
        this.difficulty = _difficulty ?: DifficultyLevel.EASY
        this.highScore = _highScore
        this.currentScore = 0
        this.timeToSelect = this.difficulty.params.timeToSelect
        this.flashSpeed = this.difficulty.params.flashSpeed
        this.sequence = ArrayList<SimonColor>()

        for (index in 0 until this.difficulty.params.initialSequenceLength) {
            addColorToSequence()
        }

        when (this.difficulty) {
            DifficultyLevel.ADVANCED -> {
                this.decrementList = arrayListOf(10, 14)
                decrementFlashSpeed()
            }
            else -> this.decrementList = arrayListOf(6, 10, 14)
        }
    }

    //MARK:- Getters
    fun getSequenceElement(index: Int): SimonColor {
        return this.sequence.elementAt(index)
    }

    fun getSequenceSize(): Int {
        return this.sequence.size
    }

    fun getHighScore(): Int {
        return this.highScore
    }

    fun getCurrentScore(): Int {
        return this.currentScore
    }

    fun getTimeToSelect(): Long {
        return this.timeToSelect
    }

    fun getFlashSpeed(): Long {
        return this.flashSpeed
    }

    fun getInitialFlashSpeed(): Long {
        return this.difficulty.params.flashSpeed
    }

    private fun getRandomColor(): SimonColor {
        val index = (Random().nextInt(SimonColor.values().size - 0) + 0)
        return SimonColor.values().elementAt(index)
    }

    //NOTE:- MB's Simon specifies that when a round is completed on
    // level 1, 2, or 3, a six-signal salute is sounded by the Simon
    // game. Here, that salute has been extended to 10 tones, as
    // illustrated by the final size of the returned array list.
    fun getVictorySong(color: SimonColor): ArrayList<SimonColor> {
        val melody = ArrayList<SimonColor>()
        melody.add(color)
        for (rawRes in 0 until 10) {
            val randomColor = getRandomColor()
            melody.add(randomColor)
        }

        return melody
    }

    //MARK:- Var Mutations
    private fun decrementFlashSpeed() {
        //NOTE:- Value determined after testing,
        // has not been sourced against MB's Simon
        this.flashSpeed -= 400
    }

    /*
    fun resetFlashSpeed() {
        this.flashSpeed = this.difficulty.params.flashSpeed
    }
    */

    private fun addColorToSequence() {
        val color = getRandomColor()
        this.sequence.add(color)
    }

    fun incrementScore() {
        this.currentScore++
        if (this.currentScore > this.highScore) {
            this.highScore = this.currentScore
        }
    }

    //MARK:- Validations
    fun validateSelection(index: Int, color: SimonColor): Boolean {
        return (this.sequence.elementAt(index) == color)
    }

    //NOTE:- If the sequence.size() == difficulty.params.maxSequenceLength,
    // the user has won the round.
    fun colorCanBeAdded(): Boolean {
        when (this.sequence.size < this.difficulty.params.maxSequenceLength) {
            true -> {
                addColorToSequence()
                return true
            }
            false -> return false
        }
    }

    //NOTE:- MB's Simon automatically speeds up after a 5th, 9th, or 13th
    // signals in a sequence are added (specified in their directions).
    // The numbers in the decrementList correspond to allowing adherence
    // to the above-stated rule.
    fun speedShouldDecrement(): Boolean {
        if (this.decrementList.size > 0) {
            when (getSequenceSize() == this.decrementList.first()) {
                true -> {
                    decrementFlashSpeed()
                    this.decrementList.removeAt(0)
                    return true
                }
                false -> return false
            }
        }

        return false
    }
}