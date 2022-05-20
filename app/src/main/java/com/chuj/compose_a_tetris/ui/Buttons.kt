package com.chuj.compose_a_tetris.ui

import android.view.KeyEvent.ACTION_DOWN
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chuj.compose_a_tetris.R
import com.chuj.compose_a_tetris.logic.Action
import com.chuj.compose_a_tetris.logic.Clickable
import com.chuj.compose_a_tetris.logic.Direction
import com.chuj.compose_a_tetris.logic.combineClickable
import com.chuj.compose_a_tetris.ui.theme.Purple40
import com.chuj.compose_a_tetris.ui.theme.Purple80
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
fun BasicButton(
    modifier: Modifier = Modifier,
    size : Dp,
    onClick: () -> Unit = {},
    content: @Composable (Modifier) -> Unit
    ) {
    val backgroundShape = CircleShape

    Button(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = backgroundShape,
    ) {
        content(Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
fun DirectionButtonAssembly(
    directionButtonSize : Dp,
    modifier: Modifier,
    onMove: (Direction) -> Unit = {}
) {

    val buttonText = @Composable {
            modifier : Modifier,
            text : String ->
        Text(
            text = text,
            color = Color.White,
            fontSize = 25.sp,
            modifier = modifier
        )
    }

    Box(modifier = modifier.size(directionButtonSize * 2.5f)
        ) {
        BasicButton(
            modifier = Modifier.align(Alignment.TopCenter),
            size = directionButtonSize,
            onClick = { onMove(Direction.Up) }
        ) {
            buttonText(it.align(Alignment.Center), stringResource(id = R.string.button_up_str))
        }
        BasicButton(
            modifier = Modifier.align(Alignment.CenterStart),
            size = directionButtonSize,
            onClick = { onMove(Direction.Left) }
        ) {
            buttonText(it, stringResource(id = R.string.button_left_str))
        }
        BasicButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            size = directionButtonSize,
            onClick = { onMove(Direction.Right) }
        ) {
            buttonText(it, stringResource(id = R.string.button_right_str))
        }
        BasicButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            size = directionButtonSize,
            onClick = { onMove(Direction.Down) }
        ) {
            buttonText(it, stringResource(id = R.string.button_down_str))
        }
    }
}

@Composable
fun RotateButton(rotateButtonSize : Dp, modifier: Modifier, onRotate: () -> Unit = {}) {
    BasicButton(
        modifier = modifier,
        size = rotateButtonSize,
        onClick = onRotate
    ) {
        Text(
            text = stringResource(id = R.string.button_rotate_str),
            color = Color.White,
            fontSize = 22.sp,
            modifier = it
        )
    }
}

@Composable
fun GameMoveController(
    clickable: Clickable = combineClickable(),
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .width(DirectionButtonSize * 2.5f + RotateButtonSize * 1.5f)
        .height(DirectionButtonSize * 2.5f)
    ) {
        DirectionButtonAssembly(
            directionButtonSize = DirectionButtonSize,
            modifier = Modifier.align(Alignment.CenterStart),
            onMove = clickable.onMove
        )

        RotateButton(
            rotateButtonSize = RotateButtonSize,
            modifier = Modifier.align(Alignment.CenterEnd),
            onRotate = clickable.onRotate
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameMoveControllerPreview() {
    GameMoveController()
}

@Composable
fun GameStateController(
    clickable: Clickable = combineClickable(),
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .padding(5.dp)
        .width(StateButtonWidth * 3f * 1.5f)
        .height(StateButtonHeight)
    ) {
        Button(
            onClick = clickable.onPause,
            modifier = Modifier
                .align(Alignment.Center)
                .height(StateButtonHeight)
                .width(StateButtonWidth)
        ) {
            Text(
                text = stringResource(id = R.string.button_pause_str),
                fontSize = 10.sp
            )
        }
        Button(
            onClick = clickable.onReset,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .height(StateButtonHeight)
                .width(StateButtonWidth)
        ) {
            Text(
                text = stringResource(id = R.string.button_reset_str),
                fontSize = 12.sp
            )
        }
        Button(
            onClick = clickable.onExit,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(StateButtonHeight)
                .width(StateButtonWidth)
        ) {
            Text(
                text = stringResource(id = R.string.button_exit_str),
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameStateControllerPreview() {
    GameStateController()
}