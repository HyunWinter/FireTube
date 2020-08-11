package com.hyun.firetube.database

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import com.hyun.firetube.R
import com.hyun.firetube.fragment.PlaylistsFragment
import com.hyun.firetube.model.Playlist
import kotlinx.android.synthetic.main.frag_playlists.view.*
import java.util.*


/************************************************************************
 * Purpose:         Async Task For Youtube API Playlist
 * Precondition:    Called from MainActivity
 * Postcondition:   Execute Youtube Service Asynchronously
 ************************************************************************/
class MakePlaylistRequestTask(context : PlaylistsFragment)
    : AsyncTask<Void?, Void?, ArrayList<Playlist>>() {

    companion object{
        private const val TAG = "MakePlaylistRequestTask"  // Logcat
        private const val DEFAULT_REQUEST_SIZE = 20L
        private const val DEFAULT_REQUEST_TYPE = "snippet,contentDetails"
        private val SCOPES = arrayOf(YouTubeScopes.YOUTUBE_READONLY)
    }

    private var mCredential : GoogleAccountCredential
    private var mService : YouTube? = null
    private var mLastError : Exception? = null
    private var mPageToken : String? = ""
    private val mContext = context

    init {
        this.mCredential = GoogleAccountCredential
            .usingOAuth2(mContext.activity?.applicationContext, listOf(*SCOPES))
            .setBackOff(ExponentialBackOff())
        val googleSignInAccount = GoogleSignIn
            .getLastSignedInAccount(mContext.activity?.applicationContext)
        this.mCredential.selectedAccount = googleSignInAccount!!.account

        val transport: HttpTransport = NetHttpTransport()
        val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
        this.mService = YouTube.Builder(transport, jsonFactory, mCredential)
            .setApplicationName(this.mContext.getString(R.string.app_name))
            .build()
    }

    /************************************************************************
     * Purpose:         Async Task In Background
     * Precondition:    .
     * Postcondition:   getDataFromApi error catching
     ************************************************************************/
    override fun doInBackground(vararg params: Void?): ArrayList<Playlist>? {

        return try {
            getDataFromApi()
        }
        catch (e: Exception) {
            mLastError = e
            cancel(true)
            null
        }
    }

    /************************************************************************
     * Purpose:         Get Data From Api
     * Precondition:    .
     * Postcondition:   Execute Youtube Service and
     *                  Take Results To onPostExecute
     ************************************************************************/
    private fun getDataFromApi() : ArrayList<Playlist> {

        val playlist : ArrayList<Playlist> = arrayListOf()

        // Max result size is 50 while max playlist size is 200
        // Since the query can't be pre-ordered (as described later),
        // You have to pull the entire playlists first

        // Since Youtube API has limited number of requests,
        // I will implement something in Firestore caching to minimize
        // wasteful reads.
        while (mPageToken != null) {

            val result = mService!!
                .playlists()
                .list(DEFAULT_REQUEST_TYPE)
                .setMine(true)
                .setMaxResults(DEFAULT_REQUEST_SIZE)
                .setPageToken(this.mPageToken)
                .execute()

            this.mPageToken = result.nextPageToken
            val playlistResults = result.items

            for (i in playlistResults.indices) {

                playlist.add(
                    Playlist(
                        playlistResults[i].id,
                        playlistResults[i].snippet.title,
                        playlistResults[i].snippet.thumbnails.medium.url,
                        playlistResults[i].contentDetails.itemCount.toInt()
                    )
                )
            }
        }

        return playlist
    }

    /************************************************************************
     * Purpose:         Before Execution
     * Precondition:    .
     * Postcondition:   show ProgressBar
     ************************************************************************/
    override fun onPreExecute() {
        this.mContext.showProgressBar(this.mContext.getRoot().Playlists_ProgressBar)
    }

    /************************************************************************
     * Purpose:         After Execution
     * Precondition:    .
     * Postcondition:   Hide ProgressBar and make SnackBar message when
     *                  no results are returned
     ************************************************************************/
    override fun onPostExecute(output : ArrayList<Playlist>) {

        this.mContext.hideProgressBar(this.mContext.getRoot().Playlists_ProgressBar)

        if (output.isEmpty()) {
            this.mContext.makeSnackBar(
                this.mContext.getRoot().Playlists_Background,
                "No results returned."
            )
        }
        else {
            val pref: SharedPreferences = this.mContext
                .requireActivity()
                .getPreferences(Context.MODE_PRIVATE)

            when (pref.getString(this.mContext.getString(R.string.Playlists_Sort_Key), this.mContext.getString(R.string.Sort_ASC_Key))) {
                this.mContext.getString(R.string.Sort_ASC_Key) -> this.mContext.sortPlayListAscending(output)
                this.mContext.getString(R.string.Sort_DES_Key) ->  this.mContext.sortPlayListDescending(output)
            }

            this.mContext.updatePlaylistAdapter(output)
        }
    }

    /************************************************************************
     * Purpose:         On Execution Canceled
     * Precondition:    .
     * Postcondition:   Hide ProgressBar and make SnackBar error message
     ************************************************************************/
    override fun onCancelled() {

        this.mContext.hideProgressBar(this.mContext.getRoot().Playlists_ProgressBar)

        if (mLastError != null) {

            if (mLastError is GooglePlayServicesAvailabilityIOException) {
                this.mContext.showGooglePlayServicesAvailabilityErrorDialog(
                    (mLastError as GooglePlayServicesAvailabilityIOException)
                        .connectionStatusCode
                )
            }
            else if (mLastError is UserRecoverableAuthIOException) {
                this.mContext.startActivityForResult(
                    (mLastError as UserRecoverableAuthIOException).intent,
                    PlaylistsFragment.REQUEST_AUTHORIZATION
                )
            }
            else {
                val errorStr = (
                        "The following error occurred: "
                        + mLastError!!.message
                    )
                    .trimIndent()
                Log.e(TAG, "The following error occurred: $errorStr")
                this.mContext.makeSnackBar(this.mContext.getRoot().Playlists_Background, errorStr)
            }
        }
        else {

            this.mContext.makeSnackBar(this.mContext.getRoot().Playlists_Background, "Request cancelled.")
        }
    }
}