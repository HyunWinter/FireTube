package com.hyun.firetube.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.hyun.firetube.R
import com.hyun.firetube.activity.VideoPlayerActivity
import com.hyun.firetube.adapter.VideoAdapter
import com.hyun.firetube.database.MakeUploadsRequestTask
import com.hyun.firetube.model.Video
import com.hyun.firetube.utility.Helper
import kotlinx.android.synthetic.main.frag_uploads.*
import kotlinx.android.synthetic.main.frag_uploads.view.*
import java.util.*


class UploadsFragment : BaseFragment(), VideoAdapter.VideoClickListener {

    // Companion
    companion object {
        private const val TAG = "VideoFragment"  // Logcat
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    }

    // Variables
    private lateinit var mUploadsAdapter : VideoAdapter
    private lateinit var mUploads : ArrayList<Video>
    private lateinit var mRoot : View

    /************************************************************************
     * Purpose:         onCreate
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {

        this.mRoot = inflater.inflate(R.layout.frag_uploads, container, false)
        this.setContents()
        this.getResultsFromApi()

        return this.mRoot
    }

    /************************************************************************
     * Purpose:         setContents
     * Precondition:    onCreate
     * Postcondition:   Contents Initialization
     ************************************************************************/
    private fun setContents() {

        this.mUploads = arrayListOf()
        this.mUploadsAdapter = VideoAdapter(
            activity?.applicationContext,
            this.mUploads,
            this
        )

        val outValue = TypedValue()
        resources.getValue(R.dimen.RecyclerViewItem_ColumnWidth, outValue, true)
        val layoutManager = GridLayoutManager(
            activity?.applicationContext,
            Helper().calcGridWidthCount(
                requireActivity().applicationContext,
                outValue.float
            )
        )
        this.mRoot.Videos_RecyclerView.setHasFixedSize(true)
        this.mRoot.Videos_RecyclerView.layoutManager = layoutManager
        this.mRoot.Videos_RecyclerView.adapter = this.mUploadsAdapter
    }

    fun getRoot() : View {
        return this.mRoot
    }

    /************************************************************************
     * Purpose:         Parcelable Video Click to VideoPlayerActivity
     * Precondition:    Video Selected
     * Postcondition:   .
     ************************************************************************/
    override fun onVideoSelected(position: Int) {

        // Load Shared Preferences
        val savedPlayerSettings = PreferenceManager
            .getDefaultSharedPreferences(activity)
            .getBoolean(getString(R.string.Settings_Youtube_Key), false)

        lateinit var intent : Intent

        if (savedPlayerSettings) {

            val youtubeURL = "https://www.youtube.com/watch?v=" + this.mUploads[position].id
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(youtubeURL)
            intent.`package` = "com.google.android.youtube"
        }
        else {
            intent = Intent(activity, VideoPlayerActivity::class.java)
            intent.putExtra(getString(R.string.Video_ID_Key), this.mUploads[position].id)
            intent.putExtra(getString(R.string.Video_Title_Key), this.mUploads[position].title)
        }

        startActivity(intent)
    }

    /************************************************************************
     * Purpose:         Update mPlaylist and Notify RecyclerView Adapter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    fun updateVideoAdapter(videos : ArrayList<Video>) {

        this.mUploads.clear()
        this.mUploads.addAll(videos)
        this.mUploadsAdapter.notifyDataSetChanged()
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
        else if (!isDeviceOnline()) {
            makeSnackBar(this.Videos_Background, "No network connection available.")
        }
        else {
            MakeUploadsRequestTask(this).execute()
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
                if (resultCode != Activity.RESULT_OK) {
                    val errorStr = "This app requires Google Play Services. " +
                            "Please install Google Play Services on your device " +
                            "and relaunch this app."
                    makeSnackBar(this.Videos_Background, errorStr)
                }
                else {
                    getResultsFromApi()
                }
            }
            REQUEST_AUTHORIZATION -> if (resultCode == Activity.RESULT_OK) {
                getResultsFromApi()
            }
        }
    }

    /************************************************************************
     * Purpose:         Sorting Algorithm
     * Precondition:    Pre-ordered query is not working in the playlists()
     *                  type. The search() type allow pre-ordered query, but
     *                  it only works for videos and not playlists.
     * Postcondition:   .
     ************************************************************************/
    fun sortVideos(uploads : ArrayList<Video>) {
        if (uploads.size > 1) {
            Collections.sort(uploads, VideosComparator())
        }
    }

    inner class VideosComparator : Comparator<Video> {
        override fun compare(upload1 : Video, upload2 : Video): Int {
            return upload1.title.compareTo(upload2.title)
        }
    }
}