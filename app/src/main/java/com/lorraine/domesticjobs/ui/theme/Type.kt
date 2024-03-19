package com.lorraine.domesticjobs.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.lorraine.domesticjobs.R

// Set of Material typography styles to start with
val Poppins = FontFamily(Font(R.font.poppins_medium))
val titleLarge = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight.Normal,
    fontSize = 32.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp,
    textAlign = TextAlign.Center
)
val smallText = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    lineHeight = 10.sp,
    letterSpacing = 0.sp,
    textAlign = TextAlign.Center
)
val bodyMediumLight = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight.ExtraLight,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 1.sp,
)
val bodyDescription = TextStyle(
fontFamily = Poppins,
fontWeight = FontWeight.Normal,
fontSize = 12.sp,
lineHeight = 20.sp,
letterSpacing = 0.sp,
)
val bodyBold = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
)
val applicationStatus = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
)
val bodySmall = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp


    ),
    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)