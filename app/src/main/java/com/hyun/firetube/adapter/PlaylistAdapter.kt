package com.hyun.firetube.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyun.firetube.R
import com.hyun.firetube.model.Playlist
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.listitem_playlists.view.*
import java.util.*
import kotlin.collections.ArrayList


/************************************************************************
 * Purpose:         Playlist Recycler View Adapter For Playlist
 * Precondition:    .
 * Postcondition:   Initiate and Assign View Holders to
 *                  XML listitem_playlists
 ************************************************************************/
class PlaylistAdapter(playlists : ArrayList<Playlist>,
                      playlistClickListener : PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>(),
    Filterable {

    companion object {
        private const val TAG = "ChannelAdapter"  // Logcat
    }

    private val mPlayLists : ArrayList<Playlist> = playlists
    private var mPlayListsAll : ArrayList<Playlist> = playlists
    private val mPlaylistClickListener = playlistClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.listitem_playlists, parent, false)

        return ViewHolder(view, this.mPlaylistClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val playlist : Playlist = this.mPlayListsAll[position]

        holder.mTitle.text = playlist.title
        holder.mItemCount.text = playlist.itemCount.toString()
        Picasso.get()
            .load(playlist.thumbnail)
            .into(holder.mThumbnail)
    }

    override fun getItemCount(): Int {

        return this.mPlayListsAll.size
    }

    /************************************************************************
     * Purpose:         View Holder For Recycler View Adapter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    inner class ViewHolder(itemView : View,
                           clickListener : PlaylistClickListener) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var mTitle : TextView = itemView.ListItem_Playlist_Title
        var mItemCount : TextView = itemView.ListItem_Playlist_ItemCount
        var mThumbnail : ImageView = itemView.ListItem_Playlist_Thumbnail
        private var mPlaylistClickListener : PlaylistClickListener = clickListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            this.mPlaylistClickListener.onPlaylistSelected(mPlayListsAll[adapterPosition])
        }
    }

    /************************************************************************
     * Purpose:         Click Listener Interface
     * Precondition:    onPlaylistSelected in PlaylistFragment
     * Postcondition:   Send Position
     ************************************************************************/
    interface PlaylistClickListener {
        fun onPlaylistSelected(playlist : Playlist)
    }

    /************************************************************************
     * Purpose:         Search Filters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    override fun getFilter() : Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()

                mPlayListsAll = if (charSearch.isEmpty()) {
                    mPlayLists
                } else {
                    val filteredList = ArrayList<Playlist>()

                    for (item in mPlayLists) {
                        val title = item.title.toLowerCase(Locale.ROOT)
                        if (title.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            filteredList.add(item)
                        }
                    }

                    filteredList
                }

                val results = FilterResults()
                results.values = mPlayListsAll
                return results
            }

            override fun publishResults(constraint : CharSequence?, results : FilterResults?) {

                mPlayListsAll = results?.values as ArrayList<Playlist>
                notifyDataSetChanged()
            }
        }
    }
}

