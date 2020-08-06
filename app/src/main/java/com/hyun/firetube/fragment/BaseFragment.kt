package com.hyun.firetube.fragment

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.hyun.firetube.model.Playlist
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    /************************************************************************
     * Purpose:         SnackBar Message
     * Precondition:    From classes extend BaseActivity
     * Postcondition:   Make SnackBar Message On A Background
     ************************************************************************/
    fun makeSnackBar(background: CoordinatorLayout, text: String) {

        Snackbar.make(background, text, Snackbar.LENGTH_SHORT).show()
    }

    /************************************************************************
     * Purpose:         On Request Permissions Result
     * Precondition:    .
     * Postcondition:   Respond to requests for permissions at runtime for
     *                  API 23 and above.
     ************************************************************************/
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }

    /************************************************************************
     * Purpose:         Check Device Online
     * Precondition:    .
     * Postcondition:   Checks whether the device currently has a network
     *                  connection.
     ************************************************************************/
    fun isDeviceOnline() : Boolean {

        val connMgr = this
            .requireActivity()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

    /************************************************************************
     * Purpose:         Acquire Google Play Services
     * Precondition:    .
     * Postcondition:   Check that Google Play services APK is installed and
     *                  up to date.
     ************************************************************************/
    fun isGooglePlayServicesAvailable() : Boolean {

        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(
            this.activity?.applicationContext
        )
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    /************************************************************************
     * Purpose:         Acquire Google Play Services
     * Precondition:    .
     * Postcondition:   Attempt to resolve a missing, out-of-date, invalid or
     *                  disabled Google Play Services installation via a user
     *                  dialog, if possible.
     ************************************************************************/
    fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(
            this.activity?.applicationContext
        )
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    /************************************************************************
     * Purpose:         Show GooglePlayServices Availability Error Dialog
     * Precondition:    .
     * Postcondition:   Display an error dialog showing that Google Play
     *                  Services is missing or out of date.
     ************************************************************************/
    fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode : Int) {

        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
            this.activity,
            connectionStatusCode,
            PlaylistsFragment.REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog.show()
    }

    /************************************************************************
     * Purpose:         EasyPermissions Related
     * Precondition:    I'm hungry
     * Postcondition:   Eat taco
     ************************************************************************/
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) { }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) { }

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