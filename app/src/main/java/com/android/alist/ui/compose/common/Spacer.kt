package com.android.alist.ui.compose.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RowSpacer(height: Int) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
    )
}

@Composable
fun ColumnSpacer(width: Int) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .width(width.dp)
    )
}
