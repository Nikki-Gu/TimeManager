package com.example.timemanager.db.model

import androidx.annotation.ColorRes
import com.example.timemanager.R

enum class Tag(
    @ColorRes val resId: Int
) {
    GRAY(R.color.dim_grey),

    PINK(R.color.pink),

    RED(R.color.red),

    INDIGO(R.color.indigo),

    BLUE(R.color.blue),

    TEAL(R.color.teal),

    GREEN(R.color.green),

    LIME(R.color.lime),

    YELLOW(R.color.yellow),

    AMBER(R.color.amber),

    ORANGE(R.color.orange),

    BROWN(R.color.brown);
}