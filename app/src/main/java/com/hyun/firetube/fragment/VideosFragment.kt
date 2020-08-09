package com.hyun.firetube.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyun.firetube.R
import com.hyun.firetube.activity.VideoPlayerActivity
import com.hyun.firetube.adapter.VideoAdapter
import com.hyun.firetube.database.MakeVideoRequestTask
import com.hyun.firetube.model.Video
import kotlinx.android.synthetic.main.frag_videos.*
import kotlinx.android.synthetic.main.frag_videos.view.*
import java.util.*
import kotlin.collections.ArrayList

class VideosFragment : BaseFragment(), VideoAdapter.VideoClickListener {

    // Companion
    companion object {
        private const val TAG = "VideoFragment"  // Logcat
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    }

    // Variables
    private lateinit var mVideosAdapter : VideoAdapter
    private lateinit var mVideos : ArrayList<Video>
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

        this.mRoot = inflater.inflate(R.layout.frag_videos, container, false)
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

        this.mVideos = arrayListOf()
        this.mVideosAdapter = VideoAdapter(activity?.applicationContext, this.mVideos, this)
        this.mRoot.Videos_RecyclerView.setHasFixedSize(true)
        this.mRoot.Videos_RecyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        this.mRoot.Videos_RecyclerView.adapter = this.mVideosAdapter
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

        val intent = Intent(activity, VideoPlayerActivity::class.java)
        intent.putExtra(getString(R.string.Video_ID_Key), this.mVideos[position].id)
        intent.putExtra(getString(R.string.Video_Title_Key), this.mVideos[position].title)
        startActivity(intent)
    }

    /************************************************************************
     * Purpose:         Update mPlaylist and Notify RecyclerView Adapter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    fun updateVideoAdapter(videos : ArrayList<Video>) {

        this.mVideos.clear()
        this.mVideos.addAll(videos)
        this.mVideosAdapter.notifyDataSetChanged()
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
            MakeVideoRequestTask(this).execute()
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
    fun sortVideos(playlistItem : ArrayList<Video>) {
        if (playlistItem.size > 1) {
            Collections.sort(playlistItem, VideosComparator())
        }
    }

    inner class VideosComparator : Comparator<Video> {
        override fun compare(o1 : Video, o2 : Video): Int {
            return o1.title.compareTo(o2.title)
        }
    }
}