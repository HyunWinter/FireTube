package com.hyun.firetube.database

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
import com.hyun.firetube.fragment.UploadsFragment
import com.hyun.firetube.model.Video
import kotlinx.android.synthetic.main.frag_uploads.view.*
import java.util.*

/************************************************************************
 * Purpose:         Async Task For Youtube API Videos
 * Precondition:    Called from MainActivity
 * Postcondition:   Execute Youtube Service Asynchronously
 ************************************************************************/
class MakeUploadsRequestTask(context : UploadsFragment)
    : AsyncTask<Void?, Void?, ArrayList<Video>>() {

    companion object{
        private const val TAG = "MakeVideoRequestTask"  // Logcat
        private const val DEFAULT_REQUEST_SIZE = 20L
        private const val DEFAULT_REQUEST_TYPE = "snippet"
        private const val DEFAULT_SRESULT_TYPE = "video"
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

        val transport : HttpTransport = NetHttpTransport()
        val jsonFactory : JsonFactory = JacksonFactory.getDefaultInstance()
        this.mService = YouTube.Builder(transport, jsonFactory, mCredential)
            .setApplicationName(this.mContext.getString(R.string.app_name))
            .build()
    }

    /************************************************************************
     * Purpose:         Async Task In Background
     * Precondition:    .
     * Postcondition:   getDataFromApi error catching
     ************************************************************************/
    override fun doInBackground(vararg params: Void?): ArrayList<Video>? {

        return try {
            getDataFromApi()
        }
        catch (e: Exception) {
            this.mLastError = e
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
    private fun getDataFromApi() : ArrayList<Video> {

        val video : ArrayList<Video> = arrayListOf()

        val result = mService!!
            .search()
            .list(DEFAULT_REQUEST_TYPE)
            .setForMine(true)
            .setType(DEFAULT_SRESULT_TYPE)
            .setMaxResults(DEFAULT_REQUEST_SIZE)
            .setPageToken(this.mPageToken)
            .execute()

        this.mPageToken = result.nextPageToken
        val videoResults = result.items

        for (i in videoResults.indices) {

            video.add(
                Video(
                    videoResults[i].id.videoId,
                    videoResults[i].snippet.title,
                    videoResults[i].snippet.thumbnails.medium.url
                )
            )
        }

        return video
    }

    /************************************************************************
     * Purpose:         Before Execution
     * Precondition:    .
     * Postcondition:   show ProgressBar
     ************************************************************************/
    override fun onPreExecute() {
        this.mContext.showProgressBar(this.mContext.getRoot().Uploads_ProgressBar)
    }

    /************************************************************************
     * Purpose:         After Execution
     * Precondition:    .
     * Postcondition:   Hide ProgressBar and make SnackBar message when
     *                  no results are returned
     ************************************************************************/
    override fun onPostExecute(output : ArrayList<Video>) {

        this.mContext.hideProgressBar(this.mContext.getRoot().Uploads_ProgressBar)

        if (output.isEmpty()) {
            this.mContext.makeSnackBar(
                this.mContext.getRoot().Uploads_Background,
                "No results returned."
            )
        }
        else {
            this.mContext.updateVideoAdapter(output)
        }
    }

    /************************************************************************
     * Purpose:         On Execution Canceled
     * Precondition:    .
     * Postcondition:   Hide ProgressBar and make SnackBar error message
     ************************************************************************/
    override fun onCancelled() {

        this.mContext.hideProgressBar(this.mContext.getRoot().Uploads_ProgressBar)

        if (mLastError != null) {

            when (mLastError) {
                is GooglePlayServicesAvailabilityIOException -> {
                    this.mContext.showGooglePlayServicesAvailabilityErrorDialog(
                        (mLastError as GooglePlayServicesAvailabilityIOException)
                            .connectionStatusCode
                    )
                }
                is UserRecoverableAuthIOException -> {
                    this.mContext.startActivityForResult(
                        (mLastError as UserRecoverableAuthIOException).intent,
                        UploadsFragment.REQUEST_AUTHORIZATION
                    )
                }
                else -> {

                    val errorStr = (
                            "The following error occurred: "
                                    + mLastError!!.message
                            )
                        .trimIndent()
                    Log.e(TAG, "The following error occurred: $errorStr")
                    this.mContext.makeSnackBar(this.mContext.getRoot().Uploads_Background, errorStr)
                }
            }
        }
        else {

            this.mContext.makeSnackBar(this.mContext.getRoot().Uploads_Background, "Request cancelled.")
        }
    }
}