package com.hyun.firetube.fragment

import android.view.View
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {

    /************************************************************************
     * Purpose:         SnackBar Message
     * Precondition:    From classes extend BaseActivity
     * Postcondition:   Make SnackBar Message On A Background
     ************************************************************************/
    fun makeSnackBar(background: CoordinatorLayout, text: String) {

        val snackbar = Snackbar.make(
            background,
            text,
            Snackbar.LENGTH_SHORT
        )

        snackbar.show()
    }

    /************************************************************************
     * Purpose:         ProgressBar
     * Precondition:    From classes extend BaseActivity
     * Postcondition:   Show ProgressBar
     ************************************************************************/
    fun showProgressBar(progressBar: ProgressBar) {
        if (progressBar.visibility == View.INVISIBLE) {
            progressBar.visibility = View.VISIBLE
        }
    }

    /************************************************************************
     * Purpose:         ProgressBar
     * Precondition:    From classes extend BaseActivity
     * Postcondition:   Hide ProgressBar
     ************************************************************************/
    fun hideProgressBar(progressBar: ProgressBar) {
        if (progressBar.visibility == View.VISIBLE) {
            progressBar.visibility = View.INVISIBLE
        }
    }
}