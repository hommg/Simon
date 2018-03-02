package com.umsl.gregoryhommert.simon

enum class DifficultyLevel(val params: InitialDifficultyParams) {
    EASY(InitialDifficultyParams(1, 3,
            1500, 3000)),
    MEDIUM(InitialDifficultyParams(3, 14,
            1500, 3000)),
    HARD(InitialDifficultyParams(5, 20,
            1500, 3000)),
    ADVANCED(InitialDifficultyParams(7, 31,
            1500, 3000))
}

//NOTE:- Enum holds values persuant to the four specified levels of difficulty
// listed in MB's Simon directions, combined with parameters specific to the
// directions set forth in Project 1 for UMSL's CMP SCI 5020 Spring 2018.