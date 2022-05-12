package com.chuj.compose_a_tetris.ui

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

private fun DrawScope.drawGrid(
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

private fun DrawScope.drawSpirit(spirit: Spirit, brickSize: Float, gridSize: Pair<Int, Int>) {
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
    val color : Color
) {
    val location : List<Offset> = shape.map { it + offset }
}

@Composable
fun BrickGrid(blockSize: Float, gridSize: Pair<Int, Int>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawGrid(blockSize, gridSize)
    }
}

@Composable
fun DrawSpiritTest(spirit: Spirit) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawSpirit(
            spirit,
            30f,
            Pair(12, 24)
        )
    }
}

val SpiritType = listOf(
    listOf(Offset(1f, -1f), Offset(1f, 0f), Offset(0f, 0f), Offset(0f, 1f)),//Z
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(1f, 0f), Offset(1f, 1f)),//S
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f), Offset(0f, 2f)),//I
    listOf(Offset(0f, 1f), Offset(0f, 0f), Offset(0f, -1f), Offset(1f, 0f)),//T
    listOf(Offset(1f, 0f), Offset(0f, 0f), Offset(1f, -1f), Offset(0f, -1f)),//O
    listOf(Offset(0f, -1f), Offset(1f, -1f), Offset(1f, 0f), Offset(1f, 1f)),//L
    listOf(Offset(1f, -1f), Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f))//J
)

val SpiritColor = listOf(
    Color.Blue,
    Color.Red,
    Color.Yellow,
    Color.Green,
    Color.Cyan,
    Color.Magenta,
    Color.Black
)