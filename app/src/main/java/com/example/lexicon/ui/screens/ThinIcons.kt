package com.example.lexicon.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ThinBookmarkOutline: ImageVector
    get() = ImageVector.Builder(
        name = "ThinBookmarkOutline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(17f, 3f)
            lineTo(7f, 3f)
            arcTo(2f, 2f, 0f, false, false, 5f, 5f)
            lineTo(5f, 21f)
            lineTo(12f, 18f)
            lineTo(19f, 21f)
            lineTo(19f, 5f)
            arcTo(2f, 2f, 0f, false, false, 17f, 3f)
            close()
        }
    }.build()

val ThinBookmarkFilled: ImageVector
    get() = ImageVector.Builder(
        name = "ThinBookmarkFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(17f, 3f)
            lineTo(7f, 3f)
            arcTo(2f, 2f, 0f, false, false, 5f, 5f)
            lineTo(5f, 21f)
            lineTo(12f, 18f)
            lineTo(19f, 21f)
            lineTo(19f, 5f)
            arcTo(2f, 2f, 0f, false, false, 17f, 3f)
            close()
        }
    }.build()

val ThinFavoriteOutline: ImageVector
    get() = ImageVector.Builder(
        name = "ThinFavoriteOutline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 21.35f)
            lineTo(10.55f, 20.03f)
            curveTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
            curveTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
            curveTo(9.24f, 3f, 10.91f, 3.81f, 12f, 5.09f)
            curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
            curveTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
            curveTo(22f, 12.28f, 18.6f, 15.36f, 13.45f, 20.04f)
            lineTo(12f, 21.35f)
            close()
        }
    }.build()

val ThinFavoriteFilled: ImageVector
    get() = ImageVector.Builder(
        name = "ThinFavoriteFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 21.35f)
            lineTo(10.55f, 20.03f)
            curveTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
            curveTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
            curveTo(9.24f, 3f, 10.91f, 3.81f, 12f, 5.09f)
            curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
            curveTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
            curveTo(22f, 12.28f, 18.6f, 15.36f, 13.45f, 20.04f)
            lineTo(12f, 21.35f)
            close()
        }
    }.build()

val ThinInfo: ImageVector
    get() = ImageVector.Builder(
        name = "ThinInfo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 22f)
            arcTo(10f, 10f, 0f, true, true, 12f, 2f)
            arcTo(10f, 10f, 0f, true, true, 12f, 22f)
            
            moveTo(12f, 11f)
            lineTo(12f, 17f)
            
            moveTo(12f, 7f)
            lineTo(12f, 7.01f)
        }
    }.build()

val ThinShare: ImageVector
    get() = ImageVector.Builder(
        name = "ThinShare",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(21f, 5f)
            arcTo(3f, 3f, 0f, true, true, 15f, 5f)
            arcTo(3f, 3f, 0f, true, true, 21f, 5f)
            
            moveTo(21f, 19f)
            arcTo(3f, 3f, 0f, true, true, 15f, 19f)
            arcTo(3f, 3f, 0f, true, true, 21f, 19f)
            
            moveTo(9f, 12f)
            arcTo(3f, 3f, 0f, true, true, 3f, 12f)
            arcTo(3f, 3f, 0f, true, true, 9f, 12f)
            
            moveTo(8.5f, 10.5f)
            lineTo(15.5f, 6.5f)
            
            moveTo(8.5f, 13.5f)
            lineTo(15.5f, 17.5f)
        }
    }.build()
