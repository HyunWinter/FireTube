package com.hyun.firetube.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener
import com.hyun.firetube.R
import com.hyun.firetube.model.Video
import kotlinx.android.synthetic.main.activity_videoplayer.*


class VideoPlayerActivity : BaseActivity() {

    // Variables
    private lateinit var mVideo : Video

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

        /*setSupportActionBar(this.VideoPlayer_Toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)*/
    }

    /************************************************************************
     * Purpose:         Youtube Player
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private fun setContents() {

        this.VideoPlayer_Youtube.play(this.mVideo.id)
        //this.VideoPlayer_Title.text = this.mVideo.title
    }
}