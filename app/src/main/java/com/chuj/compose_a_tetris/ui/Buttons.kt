package com.chuj.compose_a_tetris.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chuj.compose_a_tetris.R
import com.chuj.compose_a_tetris.ui.theme.Purple40
import com.chuj.compose_a_tetris.ui.theme.Purple80

@Composable
fun DirectionButton(
    modifier: Modifier = Modifier,
    size : Dp,
    content: @Composable (Modifier) -> Unit
    ) {
    val backgroundShape = RoundedCornerShape(size / 2)

    Box(
        modifier = modifier
            .shadow(5.dp, shape = backgroundShape)
            .size(size = size)
            .clip(backgroundShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Purple40,
                        Purple80
                    ),
                    startY = 0f,
                    endY = 80f
                )
            )
    ) {
        content(Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
fun DirectionButtonAssembly() {

    val ButtonText = @Composable {
            modifier : Modifier,
            text : String ->
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp
        )
    }

    Box(modifier = Modifier.size(DirectionButtonSize * 2.5f)
        ) {
        DirectionButton(
            modifier = Modifier.align(Alignment.TopCenter),
            size = DirectionButtonSize
        ) {
        }
        DirectionButton(
            modifier = Modifier.align(Alignment.CenterStart),
            size = DirectionButtonSize
        ) {
        }
        DirectionButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            size = DirectionButtonSize
        ) {
        }
        DirectionButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            size = DirectionButtonSize
        ) {
        }
    }
}