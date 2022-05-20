package com.chuj.compose_a_tetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chuj.compose_a_tetris.logic.Action
import com.chuj.compose_a_tetris.logic.Direction
import com.chuj.compose_a_tetris.logic.GameViewModel
import com.chuj.compose_a_tetris.logic.combineClickable
import com.chuj.compose_a_tetris.ui.*
import com.chuj.compose_a_tetris.ui.theme.Compose_a_tetrisTheme
import kotlinx.coroutines.delay
import java.lang.Long.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Compose_a_tetrisTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = viewModel<GameViewModel>()
                    val viewState = viewModel.viewState.value

                    // make the block to fall
                    LaunchedEffect(key1 = Unit) {
                        while (true) {
                            delay(
                                450 - min(viewState.linesCleared.toLong() * 10, 200))
                            viewModel.dispatch(Action.Tick)
                        }
                    }

                    GameScreen(clickable = combineClickable (
                        onMove = {direction : Direction ->
                            if(direction == Direction.Up) viewModel.dispatch(Action.DropImm)
                            else viewModel.dispatch(Action.Move(direction))
                        },
                        onReset = {
                            viewModel.dispatch(Action.Reset)
                        },
                        onRotate = {
                            viewModel.dispatch(Action.Rotate)
                        },
                        onPause = {
                            viewModel.dispatch(Action.Pause)
                        }
                    ))
                }
            }
        }
    }
}
