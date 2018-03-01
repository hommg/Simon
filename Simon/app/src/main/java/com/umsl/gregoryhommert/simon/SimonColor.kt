package com.umsl.gregoryhommert.simon

import android.support.annotation.RawRes

//MARK:- Enums
enum class SimonColor(@RawRes val rawRes: Int) {
    RED(R.raw.a_2),
    GREEN(R.raw.e_2),
    BLUE(R.raw.e_3),
    YELLOW(R.raw.c_sharp_3)
}

//NOTE:- MB's Simon assigned harmonic tones to each button on
// its game board. Each member of this enum holds a @RawRes
// reference to a tone chosen after consulting
// https://en.wikipedia.org/wiki/Simon_(game) as well as
// footage of an original Simon game being played.
// The files are named for their pitch class, and were created
// in GarageBand using a built-in synthesizer.