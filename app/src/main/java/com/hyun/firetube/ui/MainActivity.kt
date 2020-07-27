package com.hyun.firetube.ui

import android.Manifest
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import com.hyun.firetube.R
import com.hyun.firetube.adapter.PlaylistAdapter
import com.hyun.firetube.model.Playlist
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val TAG = "MainActivity"  // Logcat
        const val REQUEST_ACCOUNT_PICKER = 1000
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
        private const val PREF_ACCOUNT_NAME = "accountName"
        private val SCOPES = arrayOf(YouTubeScopes.YOUTUBE_READONLY)
    }

    // Variables
    private lateinit var mCredential: GoogleAccountCredential
    private lateinit var mPlaylistAdapter : PlaylistAdapter
    private lateinit var mPlaylist : ArrayList<Playlist>

    /************************************************************************
     * Purpose:         onCreate
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setContents()
        this.getResultsFromApi()
    }

    /************************************************************************
     * Purpose:         setContents
     * Precondition:    onCreate
     * Postcondition:   Contents Initialization
     ************************************************************************/
    private fun setContents() {

        this.mCredential = GoogleAccountCredential
            .usingOAuth2(applicationContext, listOf(*SCOPES)) // Arrays.asList(*SCOPES)
            .setBackOff(ExponentialBackOff())

        this.mPlaylist = arrayListOf()
        this.mPlaylistAdapter = PlaylistAdapter(this, this.mPlaylist)
        this.Main_RecyclerView.setHasFixedSize(true)
        this.Main_RecyclerView.layoutManager = LinearLayoutManager(this)
        this.Main_RecyclerView.adapter = this.mPlaylistAdapter
    }

    /************************************************************************
     * Purpose:         Get Results From Api
     * Precondition:    1. Google Play Services installed
     *                  2. An account was selected
     *                  3. The device currently has online access
     * Postcondition:   Attempt to call the API, after verifying that all
     *                  the preconditions are satisfied.
     ************************************************************************/
    private fun getResultsFromApi() {

        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices()
        }
        else if (mCredential!!.selectedAccountName == null) {
            chooseAccount()
        }
        else if (!isDeviceOnline()) {
            makeSnackBar(this.Main_Background, "No network connection available.")
        }
        else {
            MakeRequestTask(mCredential, this).execute()
        }
    }

    /************************************************************************
     * Purpose:         Choose Account
     * Precondition:    .
     * Postcondition:   Attempts to set the account used with the API
     *                  credentials.
     *                  If an account name was previously saved it will use
     *                  that one.
     *                  Otherwise an account picker dialog will be shown to
     *                  the user
     ************************************************************************/
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun chooseAccount() {

        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {

            val accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null)

            if (accountName != null) {
                mCredential!!.selectedAccountName = accountName
                getResultsFromApi()
            }
            else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                    mCredential!!.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER
                )
            }
        }
        else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                this,
                "This app needs to access your Google account (via Contacts).",
                REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS
            )
        }
    }

    /************************************************************************
     * Purpose:         Choose Account
     * Precondition:    Called when an activity launched here (specifically,
     *                  AccountPicker and authorization) exits
     *                  Gives you the requestCode you started it with,
     *                  the resultCode it returned, and any additional data
     *                  from it.
     * Postcondition:   Attempts to set the account used with the API
     *                  credentials.
     *                  If an account name was previously saved it will use
     *                  that one.
     *                  Otherwise an account picker dialog will be shown to
     *                  the user
     ************************************************************************/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode != RESULT_OK) {
                    val errorStr = "This app requires Google Play Services. " +
                            "Please install Google Play Services on your device " +
                            "and relaunch this app."
                    makeSnackBar(this.Main_Background, errorStr)
                }
                else {
                    getResultsFromApi()
                }
            }
            REQUEST_ACCOUNT_PICKER -> {
                if (resultCode == RESULT_OK && data != null && data.extras != null) {
                    val accountName =
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    if (accountName != null) {
                        val settings =
                            getPreferences(Context.MODE_PRIVATE)
                        val editor = settings.edit()
                        editor.putString(PREF_ACCOUNT_NAME, accountName)
                        editor.apply()
                        mCredential!!.selectedAccountName = accountName
                        getResultsFromApi()
                    }
                }
            }
            REQUEST_AUTHORIZATION -> if (resultCode == RESULT_OK) {
                getResultsFromApi()
            }
        }
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
     * Purpose:         Permissions Related
     * Precondition:    .
     * Postcondition:   Eat Taco
     ************************************************************************/
    override fun onPermissionsGranted(requestCode: Int, list: List<String?>?) {
        // Do nothing.
    }

    override fun onPermissionsDenied(requestCode: Int, list: List<String?>?) {
        // Do nothing.
    }

    /************************************************************************
     * Purpose:         Check Device Online
     * Precondition:    .
     * Postcondition:   Checks whether the device currently has a network
     *                  connection.
     ************************************************************************/
    private fun isDeviceOnline() : Boolean {

        val connMgr =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

    /************************************************************************
     * Purpose:         Acquire Google Play Services
     * Precondition:    .
     * Postcondition:   Check that Google Play services APK is installed and
     *                  up to date.
     ************************************************************************/
    private fun isGooglePlayServicesAvailable() : Boolean {

        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    /************************************************************************
     * Purpose:         Acquire Google Play Services
     * Precondition:    .
     * Postcondition:   Attempt to resolve a missing, out-of-date, invalid or
     *                  disabled Google Play Services installation via a user
     *                  dialog, if possible.
     ************************************************************************/
    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
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
    fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {

        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
            this@MainActivity,
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog.show()
    }

    /************************************************************************
     * Purpose:         Update mPlaylist and Notify RecyclerView Adapter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    fun updatePlaylistAdapter(playlist : ArrayList<Playlist>) {

        this.mPlaylist.clear()
        this.mPlaylist.addAll(playlist)
        this.mPlaylistAdapter.notifyDataSetChanged()

        Log.d(TAG, "List Count: " + this.mPlaylist.size)
        Log.d(TAG, "Adapter Count: " + this.mPlaylistAdapter.itemCount)
    }
}