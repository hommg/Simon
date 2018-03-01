package com.umsl.gregoryhommert.simon

//MARK:- Data class that holds values for varying difficulty levels
data class InitialDifficultyParams(val initialSequenceLength: Int, val maxSequenceLength: Int,
                                   val flashSpeed: Long, val timeToSelect: Long)
