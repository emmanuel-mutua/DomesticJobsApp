package com.lorraine.hiremequick.presentation.components

import android.content.Context
import android.widget.Toast

fun MyToastMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
