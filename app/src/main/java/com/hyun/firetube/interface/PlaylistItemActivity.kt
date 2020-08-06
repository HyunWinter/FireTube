package com.hyun.firetube.`interface`

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyun.firetube.R
import com.hyun.firetube.adapter.VideoAdapter
import com.hyun.firetube.database.MakePlaylistItemRequestTask
import com.hyun.firetube.model.Video
import kotlinx.android.synthetic.main.activity_playlistitem.*
import kotlinx.android.synthetic.main.frag_videos.*
import java.util.*

class PlaylistItemActivity : BaseActivity() {

    // Companion
    companion object {
        private const val TAG = "PlaylistItemActivity"  // Logcat
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    }

    // Variables
    private lateinit var mVideoAdapter : VideoAdapter
    private lateinit var mVideos : ArrayList<Video>
    private lateinit var mPlaylistID : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlistitem)

        this.setToolbar()
        this.setContents()
        this.getResultsFromApi()
    }

    /************************************************************************
     * Purpose:         Set Intent
     * Precondition:    onCreate
     * Postcondition:   Contents Initialization
     ************************************************************************/
    private fun setToolbar() {

        this.mPlaylistID = intent.getStringExtra(getString(R.string.Playlist_ID_Key)) as String
        val playlistTitle = intent.getStringExtra(getString(R.string.Playlist_Title_Key))

        setSupportActionBar(this.PlaylistItem_Toolbar)
        supportActionBar?.title = playlistTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /************************************************************************
     * Purpose:         Set Contents
     * Precondition:    onCreate
     * Postcondition:   Contents Initialization
     ************************************************************************/
    private fun setContents() {

        this.mVideos = arrayListOf()
        this.mVideoAdapter = VideoAdapter(this, this.mVideos)
        this.PlaylistItem_RecyclerView.setHasFixedSize(true)
        this.PlaylistItem_RecyclerView.layoutManager = LinearLayoutManager(this)
        this.PlaylistItem_RecyclerView.adapter = this.mVideoAdapter
    }

    /************************************************************************
     * Purpose:         Update mPlaylist and Notify RecyclerView Adapter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    fun updateVideoAdapter(videos : ArrayList<Video>) {

        this.mVideos.clear()
        this.mVideos.addAll(videos)
        this.mVideoAdapter.notifyDataSetChanged()
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
            MakePlaylistItemRequestTask(this, this.mPlaylistID).execute()
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
    fun sortVideos(videos : ArrayList<Video>) {
        if (videos.size > 1) {
            Collections.sort(videos, VideosComparator())
        }
    }

    inner class VideosComparator : Comparator<Video> {
        override fun compare(o1: Video, o2: Video): Int {
            return o1.title.compareTo(o2.title)
        }
    }

    /************************************************************************
     * Purpose:         Back Button Override
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}