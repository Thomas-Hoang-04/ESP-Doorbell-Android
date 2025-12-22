package com.thomas.doorbell.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object TextStyles {
    val heading = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )

    val label = TextStyle(
        fontSize = 12.sp,
        fontStyle = FontStyle.Normal,
        fontWeight = FontWeight.Medium
    )

    val body = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    )

    val button = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    val dialogTitle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )

    val dialogBody = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    )

    val dialogButton = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )

    val error = TextStyle(
        fontSize = 12.sp,
        color = Color.Red
    )

    val placeholder = TextStyle(
        fontSize = 12.sp,
        color = Color.Gray
    )
}
