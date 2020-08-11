package com.hyun.firetube.utility

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.preference.PreferenceManager
import com.hyun.firetube.R
import com.hyun.firetube.activity.VideoPlayerActivity
import com.hyun.firetube.model.Video
import kotlinx.android.synthetic.main.activity_videolist.*

interface VideoSelectedInterface {

    fun videoSelectedResponse(video : Video, context : Context) : Boolean {

        // Load Shared Preferences
        val savedPlayerSettings = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.Settings_Youtube_Key), false)

        lateinit var intent : Intent

        if (savedPlayerSettings) {

            val youtubeURL = context.getString(R.string.Settings_Youtube_URL) + video.id
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(youtubeURL)
            intent.`package` = context.getString(R.string.Settings_Youtube_Package)

            val manager = context.packageManager
            val infos = manager.queryIntentActivities(intent, 0)

            return if (infos.size > 0) {
                context.startActivity(intent)
                true
            } else {
                false
            }
        }
        else {
            intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra(context.getString(R.string.Video_ID_Key), video.id)
            intent.putExtra(context.getString(R.string.Video_Title_Key), video.title)
            context.startActivity(intent)
            return true
        }
    }
}