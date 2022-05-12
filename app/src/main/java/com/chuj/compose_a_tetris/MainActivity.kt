package com.chuj.compose_a_tetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import com.chuj.compose_a_tetris.ui.*
import com.chuj.compose_a_tetris.ui.theme.Compose_a_tetrisTheme
import java.nio.channels.FileLock

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
                    //Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun DefaultBrickGrid() {
    BrickGrid(blockSize = 30f, gridSize = Pair(12, 24))
}

@Composable
fun SpiritPreview() {
   SpiritType.indices.forEach { i ->
            val spirit = Spirit(
                SpiritType[i],
                Offset(3f, 3f * i),
                SpiritColor[i]
            )
            DrawSpiritTest(spirit = spirit)
    }
}

@Preview(showBackground = true)
@Composable
fun TestPreview() {
    DefaultBrickGrid()
    SpiritPreview()
}