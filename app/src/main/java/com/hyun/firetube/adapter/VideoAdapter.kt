package com.hyun.firetube.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyun.firetube.R
import com.hyun.firetube.model.Video
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

/************************************************************************
 * Purpose:         Video Recycler View Adapter For Videos
 * Precondition:    .
 * Postcondition:   Initiate and Assign View Holders to
 *                  XML listitem_videos
 ************************************************************************/
class VideoAdapter(videoList : ArrayList<Video>,
                   clickListener: VideoClickListener) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>(),
    Filterable {

    companion object {
        private const val TAG = "VideoAdapter"  // Logcat
    }

    private val mVideoLists : ArrayList<Video> = videoList
    private var mVideoListsAll : ArrayList<Video> = videoList
    private val mVideoClickListener = clickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoAdapter.ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.listitem_videos, parent, false)

        return ViewHolder(view, this.mVideoClickListener)
    }

    override fun onBindViewHolder(holder: VideoAdapter.ViewHolder, position: Int) {

        val video : Video = this.mVideoListsAll[position]

        holder.title.text = video.title
        Picasso.get().load(video.thumbnail).into(holder.thumbnail)
    }

    override fun getItemCount(): Int {

        return this.mVideoListsAll.size
    }

    /************************************************************************
     * Purpose:         View Holder For Recycler View Adapter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    inner class ViewHolder(itemView: View,
                           clickListener : VideoClickListener) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var title = itemView.findViewById<View>(R.id.Video_Title) as TextView
        var thumbnail = itemView.findViewById(R.id.Video_Thumbnail) as ImageView
        private var videoClickListener : VideoClickListener = clickListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            this.videoClickListener.onVideoSelected(mVideoListsAll[adapterPosition])
        }
    }

    /************************************************************************
     * Purpose:         Click Listener Interface
     * Precondition:    onPlaylistSelected in PlaylistFragment
     * Postcondition:   Send Position
     ************************************************************************/
    interface VideoClickListener {
        fun onVideoSelected(video : Video)
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

                mVideoListsAll = if (charSearch.isEmpty()) {
                    mVideoLists
                } else {
                    val filteredList = ArrayList<Video>()

                    for (item in mVideoLists) {
                        val title = item.title.toLowerCase(Locale.ROOT)
                        if (title.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            filteredList.add(item)
                        }
                    }

                    filteredList
                }

                val results = FilterResults()
                results.values = mVideoListsAll
                return results
            }

            override fun publishResults(constraint : CharSequence?, results : FilterResults?) {

                mVideoListsAll = results?.values as ArrayList<Video>
                notifyDataSetChanged()
            }
        }
    }
}