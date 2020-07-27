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

class VideoAdapter(context : Context, videolist : ArrayList<Video>) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "VideoAdapter"  // Logcat
    }

    private val mVideoLists : ArrayList<Video>? = videolist
    private val mContext : Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoAdapter.ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.frag_playlist_item, parent, false)

        return ViewHolder(view)
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
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<View>(R.id.Playlist_Title) as TextView
        var thumbnail = itemView.findViewById(R.id.Playlist_Thumbnail) as ImageView
    }
}