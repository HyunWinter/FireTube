package com.hyun.firetube.database

import android.os.AsyncTask
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.hyun.firetube.R
import com.hyun.firetube.fragment.PlaylistsFragment
import com.hyun.firetube.model.Playlist
import kotlinx.android.synthetic.main.frag_playlists.view.*
import java.util.*

/************************************************************************
 * Purpose:         Async Task For Youtube API
 * Precondition:    Called from MainActivity
 * Postcondition:   Execute Youtube Service Asynchronously
 ************************************************************************/
class MakePlaylistRequestTask(credential : GoogleAccountCredential?, context : PlaylistsFragment)
    : AsyncTask<Void?, Void?, ArrayList<Playlist>>() {

    companion object{
        private const val DEFAULT_REQUEST_SIZE = 20L
        private const val DEFAULT_REQUEST_TYPE = "snippet,contentDetails"
    }

    private var mService : YouTube? = null
    private var mLastError : Exception? = null
    private var mPageToken : String? = ""
    private val mContext = context

    init {
        val transport: HttpTransport = NetHttpTransport()
        val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
        this.mService = YouTube.Builder(transport, jsonFactory, credential)
            .setApplicationName(this.mContext.getString(R.string.app_name))
            .build()
    }

    /************************************************************************
     * Purpose:         Async Task In Background
     * Precondition:    .
     * Postcondition:   getDataFromApi error catching
     ************************************************************************/
    override fun doInBackground(vararg params: Void?): ArrayList<Playlist>? {

        try {
            return getDataFromApi()
        }
        catch (e: Exception) {
            mLastError = e
            cancel(true)
            return null
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
            val playlists = result.items

            for (i in playlists.indices) {

                playlist.add(
                    Playlist(
                        playlists[i].id,
                        playlists[i].snippet.title,
                        playlists[i].snippet.thumbnails.high.url,
                        playlists[i].contentDetails.itemCount.toInt()
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
            this.mContext.sortPlayList(output)
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
                val errorStr = ("The following error occurred:"
                        + mLastError!!.message).trimIndent()
                this.mContext.makeSnackBar(this.mContext.getRoot().Playlists_Background, errorStr)
            }
        }
        else {

            this.mContext.makeSnackBar(this.mContext.getRoot().Playlists_Background, "Request cancelled.")
        }
    }
}