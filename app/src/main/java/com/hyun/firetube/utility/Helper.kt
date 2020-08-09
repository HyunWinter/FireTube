package com.hyun.firetube.utility

import android.content.Context

class Helper {

    fun calcGridWidthCount(context : Context, columnWidthDp : Float) : Int {

        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }
}