package com.hyun.firetube.utility

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity() {

    fun makeSnackbar(background: CoordinatorLayout, text: String) {

        val snackbar = Snackbar.make(
            background,
            text,
            Snackbar.LENGTH_SHORT
        )

        snackbar.show()
    }

    open fun showProgressBar(progressBar: ProgressBar) {
        if (progressBar.visibility == View.INVISIBLE) {
            progressBar.visibility = View.VISIBLE
        }
    }

    open fun hideProgressBar(progressBar: ProgressBar) {
        if (progressBar.visibility == View.VISIBLE) {
            progressBar.visibility = View.INVISIBLE
        }
    }
}