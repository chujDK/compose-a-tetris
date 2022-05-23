package com.chuj.compose_a_tetris

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    simpleMainScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun simpleMainScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val context = LocalContext.current
        Text(
            text = stringResource(id = R.string.main_activity_app_name),
            fontSize = 20.sp
        )

        Button(
            onClick = {
                val intent = Intent(context, GameActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(text = stringResource(id = R.string.start_game_button_str))
        }
        
        Button(
            onClick = {
                
            }
        ) {
            Text(text = stringResource(id = R.string.show_scores_button_str))
        }
    }
}
