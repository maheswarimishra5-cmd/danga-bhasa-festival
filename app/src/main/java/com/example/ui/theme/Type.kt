package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val OriyaFontFamily = FontFamily(Font(R.font.noto_sans_oriya, FontWeight.Normal))
val CinzelDisplayFamily = FontFamily(Font(R.font.cinzel, FontWeight.Bold))

val Typography =
  Typography(
    headlineMedium =
      TextStyle(
        fontFamily = CinzelDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = OriyaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp,
      ),
  )
