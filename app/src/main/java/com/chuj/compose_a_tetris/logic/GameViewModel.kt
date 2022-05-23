package com.chuj.compose_a_tetris.logic

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.chuj.compose_a_tetris.ui.*

class GameViewModel : ViewModel() {
    private val _viewState : MutableState<ViewState> = mutableStateOf(ViewState())
    val viewState : State<ViewState> = _viewState;

    fun dispatch(action: Action) {
        _viewState.value = reduce(viewState.value, action)
    }

    private fun genRandomSpirit() : Spirit {
        val spiritID = (0 until SpiritSum).random()
        return Spirit(
            SpiritType[spiritID],
            DefaultSpiritStartOffset,
            SpiritColor[spiritID]
        )
    }

    private fun directionToOffset(direction: Direction) : Offset {
        return when (direction) {
            Direction.Down -> Offset(0f, 1f)
            Direction.Left -> Offset(-1f, 0f)
            Direction.Right -> Offset(1f, 0f)
            else -> Offset(0f, 0f)
        }
    }

    private fun locationInGrid(location : Offset) : Boolean {
        return location.x >= 0 && location.x < GridWidth &&
                location.y < GridHeight
    }

    private fun locationInBricks(location : Offset) : Boolean {
        viewState.value.bricks.forEach {
            if (location == it.offset) {
                return true
            }
        }
        return false
    }

    private fun canSpiritExistOnGrid(spirit: Spirit) : Boolean {
        spirit.location.forEach {
            if (!locationInGrid(it)) return false
            if (locationInBricks(it)) {
                return false
            }
        }
        return true
    }

    private fun canApplyMove(direction: Direction, spirit: Spirit) : Boolean {
        val offset = directionToOffset(direction = direction)
        val spiritMoved = spirit.copy(offset = spirit.offset + offset)
        return canSpiritExistOnGrid(spiritMoved)
    }

    private fun applyMove(direction: Direction, spirit: Spirit) : Spirit {
        return spirit.copy(
            offset = directionToOffset(direction = direction) + spirit.offset
        )
    }

    private fun maybeApplyMove(direction: Direction, spirit: Spirit) : Spirit {
        // here, direction won't be up, so care not about it
        return if (canApplyMove(direction = direction, spirit = spirit)) {
            applyMove(direction = direction, spirit = spirit)
        } else {
            spirit
        }
    }

    private fun maybeRotate(spirit: Spirit) : Spirit {
        val spiritRotated = spirit.rotate()
        return if (canSpiritExistOnGrid(spiritRotated)) {
            spiritRotated
        } else {
            spirit
        }
    }

    private fun maybeGameOver(state: ViewState) : ViewState {
        state.spirit.location.forEach {
            if (it.y <= 0) {
                // set the game over
                return state.copy(gameStatus = GameStatus.GameOver)
            }
        }
        return state
    }

    private fun maybeCleanLine(state: ViewState) : ViewState {
        val brickListSortedByHeight = mutableListOf<MutableList<Brick>>()
        (0 until GridHeight).forEach { _ ->
            brickListSortedByHeight.add(mutableListOf())
        }
        var linesClearedCurrent = 0
        var linesClearedSum = 0
        var pointsEarned = 0
        val heightAdjust = Array(GridHeight) { 0 }

        state.bricks.forEach {
            brickListSortedByHeight[it.offset.y.toInt()].add(it)
        }

        for (i in brickListSortedByHeight.indices) {
            if (brickListSortedByHeight[i].size == GridWidth) {
                linesClearedCurrent++
                (0 until i).forEach { idx ->
                    heightAdjust[idx]++
                }
                brickListSortedByHeight[i] = mutableListOf()
            } else {
                linesClearedSum += linesClearedCurrent
                pointsEarned += PointsForCleaningLines[linesClearedCurrent]
                linesClearedCurrent = 0
            }
        }

        // have to do this if the line is the last line
        linesClearedSum += linesClearedCurrent
        pointsEarned += PointsForCleaningLines[linesClearedCurrent]

        val newBricks = mutableListOf<Brick>()

        brickListSortedByHeight.forEach { mutableList ->
            mutableList.forEach {
                newBricks.add(it.copy(
                    offset = Offset(
                        it.offset.x,
                        it.offset.y + heightAdjust[it.offset.y.toInt()]
                        )
                    )
                )
            }
        }

        return state.copy(
            bricks = newBricks,
            linesCleared = linesClearedSum + state.linesCleared,
            score = pointsEarned + state.score
        )
    }

    private fun maybeDropOrJoinCurrentSpiritToBricks(state: ViewState) : ViewState {
        // if current spirit can drop, skip the join
        return if (canApplyMove(direction = Direction.Down, spirit = state.spirit)) {
            // only drop
             state.copy(
                spirit = maybeApplyMove(
                    direction = Direction.Down,
                    spirit = state.spirit
                )
            )
        } else {
            // join now

            // A. first check if the spirit is out of grid
            var newState = maybeGameOver(state)
            if (newState.gameStatus == GameStatus.GameOver) return newState

            // B. add the spirit to the bricks
            val newBricks = state.bricks.toMutableList()
            val color = state.spirit.color
            state.spirit.location.forEach {
                newBricks.add(Brick(it, color))
            }

            newState = state.copy(
                spirit = state.nextSpirit,
                nextSpirit = genRandomSpirit(),
                bricks = newBricks
            )

            // C. try a line click
            newState = maybeCleanLine(newState)

            newState
        }
    }

    fun reset() {
        _viewState.value = ViewState()
    }

    private fun reduce(state: ViewState, action: Action): ViewState =
        when(action) {
            Action.Reset -> {
                if (state.isGameOver) {
                    state
                } else {
                ViewState().copy(
                    gameStatus = GameStatus.Running,
                    spirit = genRandomSpirit(),
                    nextSpirit = genRandomSpirit(),
                )
                }
            }

            is Action.Move -> run {
                if (!state.isRunning)  return@run state
                else {
                    return@run state.copy(
                        spirit = maybeApplyMove(action.direction, state.spirit)
                    )
                }
            }

            Action.Pause -> {
                if (state.isRunning) {
                    state.copy(
                        gameStatus = GameStatus.Paused
                    )
                } else if (state.isPaused) {
                    state.copy(
                        gameStatus = GameStatus.Running
                    )
                } else {
                    state
                }
            }

            Action.Tick -> {
                if (state.isRunning) {
                    maybeDropOrJoinCurrentSpiritToBricks(state)
                } else if (state.isGameOver){
                    // TODO: add the game over logic
                    state
                } else {
                    state
                }
            }

            Action.Rotate -> {
                if (state.isRunning) {
                    state.copy(spirit = maybeRotate(state.spirit))
                } else {
                    state
                }
            }

            Action.DropImm -> {
                if (state.isRunning) {
                    var newState = state
                    while (canApplyMove(Direction.Down, newState.spirit)) {
                        newState =
                            newState.copy(spirit = applyMove(Direction.Down, newState.spirit))
                    }
                    maybeDropOrJoinCurrentSpiritToBricks(newState)
                } else {
                    state
                }
            }

            Action.Exit -> {
                state
            }
        }
    }