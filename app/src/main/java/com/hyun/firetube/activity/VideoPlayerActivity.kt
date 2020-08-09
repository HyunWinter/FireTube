package com.hyun.firetube.activity

import android.os.Bundle
import com.google.api.services.youtube.YouTube
import com.hyun.firetube.R
import com.google.android.youtube.player.*
import com.hyun.firetube.model.Video
import kotlinx.android.synthetic.main.activity_playlistitem.*
import kotlinx.android.synthetic.main.activity_videoplayer.*
import java.util.ArrayList

class VideoPlayerActivity : BaseActivity() {

    // Variables
    private lateinit var mVideo : Video
    private lateinit var mYoutubePlayer : YouTubePlayerView
    private lateinit var mInitializedListener : YouTubePlayer.OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoplayer)

        this.setToolbar()
        this.setContents()
    }

    /************************************************************************
     * Purpose:         Set Intent
     * Precondition:    onCreate
     * Postcondition:   Contents Initialization
     ************************************************************************/
    private fun setToolbar() {

        val videoID = intent.getStringExtra(getString(R.string.Video_ID_Key)) as String
        val videoTitle = intent.getStringExtra(getString(R.string.Video_Title_Key)) as String
        this.mVideo = Video(videoID, videoTitle, "")

        setSupportActionBar(this.VideoPlayer_Toolbar)
        supportActionBar?.title = this.mVideo.title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setContents() {

        // TODO video player
        /*this.mInitializedListener = YouTubePlayer.OnInitializedListener { _ ->

        }*/
        /*this.VideoPlayer_Youtube.initialize(
            getString(R.string.default_web_client_id),


        )*/
    }

    /************************************************************************
     * Purpose:         Back Button Override
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}