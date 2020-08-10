package com.hyun.firetube.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.hyun.firetube.R
import com.hyun.firetube.activity.VideoListActivity
import com.hyun.firetube.adapter.PlaylistAdapter
import com.hyun.firetube.database.MakePlaylistRequestTask
import com.hyun.firetube.model.Playlist
import com.hyun.firetube.utility.Helper
import kotlinx.android.synthetic.main.frag_playlists.*
import kotlinx.android.synthetic.main.frag_playlists.view.*
import java.util.*

class PlaylistsFragment : BaseFragment(), PlaylistAdapter.PlaylistClickListener {

    // Companion
    companion object {
        private const val TAG = "PlaylistFragment"  // Logcat
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    }

    // Variables
    private lateinit var mPlaylistsAdapter : PlaylistAdapter
    private lateinit var mPlaylists : ArrayList<Playlist>
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

        this.mRoot = inflater.inflate(R.layout.frag_playlists, container, false)
        setHasOptionsMenu(true)
        this.setContents()
        if (this.mPlaylists.isEmpty()) this.getResultsFromApi()

        return this.mRoot
    }

    /************************************************************************
     * Purpose:         setContents
     * Precondition:    onCreate
     * Postcondition:   Contents Initialization
     ************************************************************************/
    private fun setContents() {

        this.mPlaylists = arrayListOf()
        this.mPlaylistsAdapter = PlaylistAdapter(
            activity?.applicationContext,
            this.mPlaylists,
            this)

        val outValue = TypedValue()
        resources.getValue(R.dimen.RecyclerViewItem_ColumnWidth, outValue, true)
        val layoutManager = GridLayoutManager(
            activity?.applicationContext,
            Helper().calcGridWidthCount(
                requireActivity().applicationContext,
                outValue.float
            )
        )
        this.mRoot.Playlists_RecyclerView.setHasFixedSize(true)
        this.mRoot.Playlists_RecyclerView.layoutManager = layoutManager
        this.mRoot.Playlists_RecyclerView.adapter = this.mPlaylistsAdapter
    }

    fun getRoot() : View {
        return this.mRoot
    }

    /************************************************************************
     * Purpose:         Parcelable Playlist Click to PlaylistItem Activity
     * Precondition:    Playlist Selected
     * Postcondition:   .
     ************************************************************************/
    override fun onPlaylistSelected(position: Int) {

        val intent = Intent(activity, VideoListActivity::class.java)
        intent.putExtra(getString(R.string.Playlist_ID_Key), this.mPlaylists[position].id)
        intent.putExtra(getString(R.string.Playlist_Title_Key), this.mPlaylists[position].title)
        startActivity(intent)
    }

    /************************************************************************
     * Purpose:         Update mPlaylist and Notify RecyclerView Adapter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    fun updatePlaylistAdapter(playlist : ArrayList<Playlist>) {

        this.mPlaylists.clear()
        this.mPlaylists.addAll(playlist)
        this.mPlaylistsAdapter.notifyDataSetChanged()
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
            makeSnackBar(this.Playlists_Background, "No network connection available.")
        }
        else {
            MakePlaylistRequestTask(this).execute()
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
                    makeSnackBar(this.Playlists_Background, errorStr)
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
    fun sortPlayList(playlist : ArrayList<Playlist>) {
        if (playlist.size > 1) {
            Collections.sort(playlist, PlaylistComparator())
        }
    }

    inner class PlaylistComparator : Comparator<Playlist> {
        override fun compare(o1: Playlist, o2: Playlist): Int {
            return o1.title.compareTo(o2.title)
        }
    }

    /************************************************************************
     * Purpose:         On Create Options Menu
     * Precondition:    When menu is constructed
     * Postcondition:   Inflate menu items
     ************************************************************************/
    override fun onCreateOptionsMenu(menu : Menu, inflater : MenuInflater) {

        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {

        when (item.itemId) {
            R.id.menu_main_refresh -> this.getResultsFromApi()
        }

        return super.onOptionsItemSelected(item)
    }
}