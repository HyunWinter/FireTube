package com.hyun.firetube.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyun.firetube.R
import com.hyun.firetube.model.Video

/************************************************************************
 * Purpose:         Video Recycler View Adapter For Videos
 * Precondition:    .
 * Postcondition:   Initiate and Assign View Holders to
 *                  XML listitem_videos
 ************************************************************************/
class VideoAdapter(context : Context?,
                   videoList : ArrayList<Video>,
                   clickListener: VideoClickListener) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "VideoAdapter"  // Logcat
    }

    private val mVideoLists : ArrayList<Video>? = videoList
    private val mContext : Context? = context
    private val mVideoClickListener = clickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoAdapter.ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.listitem_videos, parent, false)

        return ViewHolder(view, this.mVideoClickListener)
    }

    override fun onBindViewHolder(holder: VideoAdapter.ViewHolder, position: Int) {

        var video : Video = this.mVideoLists!![position]

        holder.title.text = video.title
        Glide.with(mContext!!).load(video.thumbnail).into(holder.thumbnail)
    }

    override fun getItemCount(): Int {

        return this.mVideoLists!!.size
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
            this.videoClickListener.onVideoSelected(adapterPosition)
        }
    }

    /************************************************************************
     * Purpose:         Click Listener Interface
     * Precondition:    onPlaylistSelected in PlaylistFragment
     * Postcondition:   Send Position
     ************************************************************************/
    interface VideoClickListener {
        fun onVideoSelected(position: Int)
    }
}