package com.chuj.compose_a_tetris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chuj.compose_a_tetris.logic.*
import com.chuj.compose_a_tetris.ui.GameOverAlert
import com.chuj.compose_a_tetris.ui.GameScreen
import com.chuj.compose_a_tetris.ui.theme.Compose_a_tetrisTheme
import kotlinx.coroutines.delay
import java.lang.Long.min
import kotlin.Long

class GameActivity : ComponentActivity() {
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
                    val context = LocalContext.current

                    // tell the user when the game is over
                    if (viewState.isGameOver) {
                        GameOverAlert()
                    }

                    // make the block to fall
                    LaunchedEffect(key1 = Unit) {
                        while (true) {
                            delay(
                                450 - min(viewState.linesCleared.toLong() * 10, 250)
                            )
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
                        },
                        onExit = {
                            // go the main activity
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    ))
                }
            }
        }
    }
}