package com.chuj.compose_a_tetris.ui

import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chuj.compose_a_tetris.logic.Clickable
import com.chuj.compose_a_tetris.logic.GameViewModel
import com.chuj.compose_a_tetris.logic.combineClickable
import java.lang.Float.min

@Composable
fun GridScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel<GameViewModel>()
    val viewState = viewModel.viewState.value


    Box(
        modifier = modifier
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val brickSize = min(
                size.width / viewState.grid.first,
                size.height / viewState.grid.second)


            drawGrid(blockSize = brickSize, gridSize = viewState.grid)
            drawBricks(brickSize = brickSize, bricks = viewState.bricks)
            drawSpirit(
                spirit = viewState.spirit,
                brickSize = brickSize,
                gridSize = viewState.grid)
        }
    }
}

@Composable
fun NextSpiritScreen(modifier: Modifier = Modifier) {

    val viewModel = viewModel<GameViewModel>()
    val viewState = viewModel.viewState.value
    val nextSpiritGridSize = 4 to 4

    Canvas(modifier = modifier.fillMaxSize()) {
        val brickSize = min(
            size.width / 4,
            size.height / 4
        )

        drawGrid(blockSize =  brickSize, gridSize = nextSpiritGridSize)
        drawSpirit(
            spirit = viewState.nextSpirit.copy(offset = Offset(1f, 1f)),
            brickSize = brickSize,
            gridSize = nextSpiritGridSize
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayStrAndIntCenterAligned(
    text : String = "6 * 7 = ",
    int : Int = 42,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(2.dp)
        )

        Text(
            text = int.toString(),
            modifier = Modifier
                .padding(2.dp)
        )
    }
}

/*
    <string name="score_hint_str">Current Points\u2193</string>
    <string name="lines_cleared_str">Lines Cleared\u2193</string>
    don't know why R.string has no reference
 */

const val SCORE_HINT_STR = "Current Points\u2193"
const val LINES_CLEARED_STR= "Lines Cleared\u2193"

@Composable
fun DisplayScore(modifier: Modifier = Modifier) {
    val viewModel = viewModel<GameViewModel>()
    val viewState = viewModel.viewState.value

    DisplayStrAndIntCenterAligned(
        modifier = modifier,
        text = SCORE_HINT_STR,
        int = viewState.score
    )
}

@Composable
fun DisplayLinesCleared(modifier: Modifier = Modifier) {

    val viewModel = viewModel<GameViewModel>()
    val viewState = viewModel.viewState.value

    DisplayStrAndIntCenterAligned(
        modifier = modifier,
        text = LINES_CLEARED_STR, // FIXME: use the strings.xml
        int = viewState.linesCleared
    )
}

@Composable
fun GameScreen(clickable: Clickable = combineClickable(), modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .fillMaxSize()
        .padding(10.dp)

    ) {
        Row(modifier = Modifier
            .align(Alignment.TopCenter)
            .height(400.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GridScreen(
                modifier = Modifier
                    .width(200.dp)
                    .height(400.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Next\u2193", // don't know why the stringResources can't work here
                    fontSize = 16.sp,
                    modifier = Modifier
                )

                NextSpiritScreen(
                    modifier = Modifier
                        .padding(10.dp)
                        .width(200.dp / (GridWidth / 4))
                        .height(200.dp / (GridWidth / 4))
                )

                DisplayLinesCleared(
                    modifier = Modifier
                        .padding(20.dp)
                )

                DisplayScore(
                    modifier = Modifier
                        .padding(20.dp)
                )

            }
        }

        Column (modifier = Modifier
            .align(Alignment.BottomCenter)
        ){
            GameStateController(clickable = clickable)
            GameMoveController(clickable = clickable)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    GameScreen()
}

@Composable
fun SpiritPreview(brickSize : Float) {
    SpiritType.indices.forEach { i ->
        val spirit = Spirit(
            SpiritType[i],
            Offset(3f, 4f * i + 2),
            SpiritColor[i]
        )
        DrawSpiritTest(spirit = spirit, brickSize = brickSize)
    }
}
