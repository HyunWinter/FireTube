package com.hyun.firetube.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.hyun.firetube.R
import com.hyun.firetube.adapter.VideoAdapter
import com.hyun.firetube.database.MakeUploadsRequestTask
import com.hyun.firetube.model.Video
import com.hyun.firetube.utility.Helper
import com.hyun.firetube.utility.VideoSelectedInterface
import kotlinx.android.synthetic.main.frag_uploads.*
import kotlinx.android.synthetic.main.frag_uploads.view.*
import java.util.*


class UploadsFragment :
    BaseFragment(),
    VideoAdapter.VideoClickListener,
    VideoSelectedInterface,
    SearchView.OnQueryTextListener {

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
        setHasOptionsMenu(true)
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
        this.mRoot.Uploads_RecyclerView.setHasFixedSize(true)
        this.mRoot.Uploads_RecyclerView.layoutManager = layoutManager
        this.mRoot.Uploads_RecyclerView.adapter = this.mUploadsAdapter
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

        if (!videoSelectedResponse(this.mUploads[position], requireContext().applicationContext))
        {
            makeSnackBar(this.Uploads_Background, getString(R.string.Settings_Youtube_Player_Error))
        }
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
            makeSnackBar(this.Uploads_Background, "No network connection available.")
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
    override fun onCreateOptionsMenu(menu : Menu, inflater : MenuInflater) {

        inflater.inflate(R.menu.menu_main, menu)

        val searchItem : MenuItem = menu.findItem((R.id.menu_search))
        val searchView : SearchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(this)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_sort_ascending).isVisible = false
        menu.findItem(R.id.menu_sort_descending).isVisible = false
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {

        when (item.itemId) {
            R.id.menu_refresh -> this.getResultsFromApi()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?) : Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?) : Boolean {

        this.mUploadsAdapter.filter.filter(newText)
        return false
    }
}