package com.chuj.compose_a_tetris.logic

import android.graphics.Path
import com.chuj.compose_a_tetris.ui.Brick
import com.chuj.compose_a_tetris.ui.GridHeight
import com.chuj.compose_a_tetris.ui.GridWidth
import com.chuj.compose_a_tetris.ui.Spirit
import com.chuj.compose_a_tetris.ui.Spirit.Companion.Empty

enum class Direction {
    Up,
    Down,
    Left,
    Right,
}

enum class GameStatus {
    OnBoard,
    Running,
    Paused,
    GameOver,
    OnGameOverAnimation,
}

data class Clickable constructor(
    val onMove: (Direction) -> Unit,
    val onRotate: () -> Unit,
    val onPause: () -> Unit,
    val onReset: () -> Unit,
    val onExit: () -> Unit,
)

fun combineClickable (
    onMove: (Direction) -> Unit = {},
    onRotate: () -> Unit = {},
    onPause: () -> Unit = {},
    onReset: () -> Unit = {},
    onExit: () -> Unit = {},
) = Clickable(onMove, onRotate, onPause, onReset, onExit)

sealed interface Action {
    data class Move(val direction: Direction) : Action
    object Reset : Action
    object Pause : Action
    object Rotate : Action
    object DropImm : Action
    object Exit : Action
    object Tick: Action
}

data class ViewState(
    val bricks : List<Brick> = emptyList(),
    val spirit: Spirit = Empty,
    val nextSpirit : Spirit = Empty,
    val grid : Pair<Int, Int> = GridWidth to GridHeight,
    val gameStatus : GameStatus = GameStatus.OnBoard,
    val score : Int = 0,
    val linesCleared : Int = 0,
) {
    val isRunning
        get() = gameStatus == GameStatus.Running

    val isPaused
        get() = gameStatus == GameStatus.Paused

    val isOnBoard
        get() = gameStatus == GameStatus.OnBoard

    val isGameOver
        get() = gameStatus == GameStatus.GameOver

    val isOnGameOverAnimation
        get() = gameStatus == GameStatus.OnGameOverAnimation
}