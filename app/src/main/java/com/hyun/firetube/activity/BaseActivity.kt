package com.hyun.firetube.activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.hyun.firetube.fragment.PlaylistsFragment
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

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
    @SuppressWarnings("DEPRECATION")
    fun isDeviceOnline() : Boolean {

        var result = false
        val connMgr = this
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connMgr.run {
                connMgr.getNetworkCapabilities(connMgr.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        }
        else {
            connMgr.run {
                connMgr.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    }
                    else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }

        return result
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
            this.applicationContext
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
            this.applicationContext
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
            this,
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

    /************************************************************************
     * Purpose:         Back Button Override
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}