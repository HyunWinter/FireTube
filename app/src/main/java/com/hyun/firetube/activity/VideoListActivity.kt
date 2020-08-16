package com.hyun.firetube.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.hyun.firetube.R
import com.hyun.firetube.adapter.VideoAdapter
import com.hyun.firetube.database.MakeVideoListRequestTask
import com.hyun.firetube.model.Video
import com.hyun.firetube.utility.Helper
import com.hyun.firetube.utility.VideoSelectedInterface
import kotlinx.android.synthetic.main.activity_videolist.*
import kotlinx.android.synthetic.main.frag_uploads.*
import java.util.*


class VideoListActivity : BaseActivity(), VideoAdapter.VideoClickListener, VideoSelectedInterface,
    SearchView.OnQueryTextListener {

    // Companion
    companion object {
        private const val TAG = "PlaylistItemActivity"  // Logcat
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    }

    // Variables
    private lateinit var mVideoListAdapter : VideoAdapter
    private lateinit var mVideoList : ArrayList<Video>
    private lateinit var mPlayListID : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videolist)

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

        this.mPlayListID = intent.getStringExtra(getString(R.string.Playlists_ID_Key)) as String
        val playlistTitle = intent.getStringExtra(getString(R.string.Playlists_Title_Key))

        setSupportActionBar(this.VideoList_Toolbar)
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

        this.mVideoList = arrayListOf()
        this.mVideoListAdapter = VideoAdapter(
            this,
            this.mVideoList,
            this
        )

        val outValue = TypedValue()
        resources.getValue(R.dimen.RecyclerViewItem_ColumnWidth, outValue, true)
        val layoutManager = GridLayoutManager(
            this,
            Helper().calcGridWidthCount(
                this,
                outValue.float
            )
        )
        this.VideoList_RecyclerView.setHasFixedSize(true)
        this.VideoList_RecyclerView.layoutManager = layoutManager
        this.VideoList_RecyclerView.adapter = this.mVideoListAdapter
    }

    /************************************************************************
     * Purpose:         Update mPlaylist and Notify RecyclerView Adapter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    fun updateVideoAdapter(videos : ArrayList<Video>) {

        this.mVideoList.clear()
        this.mVideoList.addAll(videos)
        this.mVideoListAdapter.notifyDataSetChanged()
    }

    /************************************************************************
     * Purpose:         Parcelable Video Click to VideoPlayerActivity
     * Precondition:    Video Selected
     * Postcondition:   .
     ************************************************************************/
    override fun onVideoSelected(video : Video) {

        if (!videoSelectedResponse(video, this))
        {
            makeSnackBar(this.VideoList_Background, getString(R.string.Settings_Youtube_Player_Error))
        }
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
            makeSnackBar(this.Uploads_Background, "No network connection available.")
        }
        else {
            MakeVideoListRequestTask(this, this.mPlayListID).execute()
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
                    makeSnackBar(this.Uploads_Background, errorStr)
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
     * Purpose:         On Create Options Menu
     * Precondition:    When menu is constructed
     * Postcondition:   Inflate menu items
     ************************************************************************/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        val searchItem : MenuItem = menu.findItem((R.id.menu_search))
        val searchView : SearchView = searchItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {

        when (item.itemId) {
            R.id.menu_refresh -> this.getResultsFromApi()
            R.id.menu_sort_ascending -> {
                val pref = getPreferences(Context.MODE_PRIVATE)
                val edt = pref.edit()
                edt.putString(getString(R.string.VideoList_Sort_Key), getString(R.string.Sort_ASC_Key))
                edt.apply()

                this.sortVideoListAscending(this.mVideoList)
                this.mVideoListAdapter.notifyDataSetChanged()
            }
            R.id.menu_sort_descending -> {
                val pref = getPreferences(Context.MODE_PRIVATE)
                val edt = pref.edit()
                edt.putString(getString(R.string.VideoList_Sort_Key), getString(R.string.Sort_DES_Key))
                edt.apply()

                this.sortVideoListDescending(this.mVideoList)
                this.mVideoListAdapter.notifyDataSetChanged()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?) : Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?) : Boolean {

        this.mVideoListAdapter.filter.filter(newText)
        return false
    }

    /************************************************************************
     * Purpose:         Sorting Algorithm
     * Precondition:    Pre-ordered query is not working in the playlists()
     *                  type. The search() type allow pre-ordered query, but
     *                  it only works for videos and not playlists.
     * Postcondition:   .
     ************************************************************************/
    fun sortVideoListAscending(videos : ArrayList<Video>) {
        if (videos.size > 1) {
            Collections.sort(videos, VideoListComparator())
        }
    }

    fun sortVideoListDescending(videos : ArrayList<Video>) {
        if (videos.size > 1) {
            Collections.sort(videos, Collections.reverseOrder(VideoListComparator()))
        }
    }

    inner class VideoListComparator : Comparator<Video> {
        override fun compare(o1: Video, o2: Video): Int {
            return o1.title.compareTo(o2.title)
        }
    }
}