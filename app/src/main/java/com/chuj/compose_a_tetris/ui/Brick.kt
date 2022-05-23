package com.chuj.compose_a_tetris.ui

import android.support.v4.os.IResultReceiver
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect


// this function can draw a single Brick
// to a single Brick, there is there part
// |--------|
// ||------||
// |||----|||
// |||    |||
// |||----|||
// ||------||
// |--------|
// 1.0 0.8 0.5
// here we should draw the inner two part
// named inner part and outer part
private fun DrawScope.drawBrick(
    brickSize : Float,
    relativeOffset : Offset,
    color : Color
) {
    val location = Offset(
        relativeOffset.x * brickSize,
        relativeOffset.y * brickSize
    )

    val outerSize = brickSize * 0.8f
    val outerOffset = (brickSize - outerSize) / 2f

    drawRect(
        color = color,
        topLeft = location + Offset(outerOffset, outerOffset),
        size = Size(outerSize, outerSize),
        style = Stroke(outerSize / 10f)
    )

    val innerSize = brickSize * 0.5f
    val innerOffset = (brickSize - innerSize) / 2f

    drawRect(
        color = color,
        topLeft = location + Offset(innerOffset, innerOffset),
        size = Size(innerSize, innerSize),
    )
}

fun DrawScope.drawBrick(
    brickSize: Float,
    brick : Brick
) {
    drawBrick(brickSize, brick.offset, brick.color)
}

fun DrawScope.drawBricks(
    brickSize: Float,
    bricks: List<Brick>
) {
    bricks.forEach {
        drawBrick(brickSize, it)
    }
}

fun DrawScope.drawGrid(
    blockSize : Float,
    gridSize : Pair<Int, Int>
) {
    (0 until gridSize.first).forEach { x ->
        (0 until gridSize.second).forEach { y ->
            drawBrick(
                blockSize,
                Offset(x.toFloat(), y.toFloat()),
                BrickGrid
            )
        }
    }
}

fun DrawScope.drawSpirit(spirit: Spirit, brickSize: Float, gridSize: Pair<Int, Int>) {
    clipRect(0f, 0f, gridSize.first * brickSize, gridSize.second * brickSize) {
        spirit.location.forEach {
            drawBrick(
                brickSize,
                it,
                spirit.color
            )
        }
    }
}

data class Spirit(
    val shape : List<Offset> = emptyList(),
    val offset: Offset = Offset(0f, 0f),
    val color : Color = BrickGrid
) {
    val location : List<Offset> = shape.map { it + offset }

    companion object {
        val Empty = Spirit()
    }

    fun rotate() : Spirit {
        val newShape = shape.toMutableList()
        for (i in shape.indices) {
            newShape[i] = Offset(shape[i].y, -shape[i].x)
        }
        return copy(shape = newShape)
    }
}

data class Brick(
    val offset: Offset,
    val color: Color
)

@Composable
fun BrickGrid(blockSize: Float, gridSize: Pair<Int, Int>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawGrid(blockSize, gridSize)
    }
}

@Composable
fun DrawSpiritTest(spirit: Spirit, brickSize: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawSpirit(
            spirit,
            brickSize,
            Pair(12, 24)
        )
    }
}

val SpiritType = listOf(
    listOf(Offset(1f, -1f), Offset(1f, 0f), Offset(0f, 0f), Offset(0f, 1f)),//Z
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f), Offset(0f, 2f)),//I
    listOf(Offset(0f, 1f), Offset(0f, 0f), Offset(0f, -1f), Offset(1f, 0f)),//T
    listOf(Offset(1f, 0f), Offset(0f, 0f), Offset(1f, -1f), Offset(0f, -1f)),//O
    listOf(Offset(1f, -1f), Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f)),//J

    // here starts the unwanted
    listOf(Offset(0f, -1f), Offset(1f, -1f), Offset(1f, 0f), Offset(1f, 1f)),//L
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(1f, 0f), Offset(1f, 1f)),//S

)

val SpiritColor = listOf(
    Color.Blue,
    Color.Red,
    Color.Yellow,
    Color.Green,
    Color.Magenta,
    Color.Cyan,
    Color.Black
)

// NOTE: here we hard coded the `SpiritSum' to 5. So there will be only the five type of spirit
// however, simply change it to `SpiritType.size` can enable all the Spirits
// val SpiritSum = 5
val SpiritSum = SpiritType.size

val DefaultSpiritStartOffset = Offset(GridWidth / 2f - 1, 0f)