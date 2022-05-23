package com.chuj.compose_a_tetris.logic

import android.provider.BaseColumns

object ScoreContract {

    object ScoreEntry : BaseColumns {
        const val TABLE_NAME = "TetrisScore"
        const val COLUMN_TIME = "time"
        const val COLUMN_NAME = "name"
        const val COLUMN_SCORE = "score"
    }

    data class Record(val score : Int, val currentTimeSecond : Long, val name : String)
}