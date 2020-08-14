package com.hyun.firetube.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.hyun.firetube.R
import com.hyun.firetube.utility.LocaleHelper
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_appbar.*

/************************************************************************
 * Purpose:         Firetube Main Activity
 * Structure:       0.1 AuthActivity : BaseActivity -> MainActivity
 *
 *                  1.1 MainActivity -> PlaylistFragment
 *                  1.2 MainActivity -> UploadsFragment
 *                  1.3 MainActivity -> SettingsFragment
 *
 *                  2.1 PlaylistFragment -> VideoListActivity
 *                  2.3 VideoListActivity -> VideoPlayerActivity
 *
 *                  3.1 UploadsFragment -> VideoPlayerActivity
 *
 *                  4.1 SettingsFragment -> AuthActivity
 *
 ************************************************************************/
class MainActivity : AppCompatActivity() {

    private lateinit var mAppBarConfig : AppBarConfiguration
    private lateinit var mAuthInstance : FirebaseAuth
    private lateinit var mInitLocale : String

    /************************************************************************
     * Purpose:         onCreate
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        this.setContents()
        this.setNavHeader()
    }

    /************************************************************************
     * Purpose:         Set Contents
     * Precondition:    .
     * Postcondition:   Initialize default contents
     ************************************************************************/
    private fun setContents() {

        // Locale
        this.mInitLocale = LocaleHelper.getPersistedLocale(this) as String

        // Toolbar
        setSupportActionBar(this.Main_Appbar_Toolbar)

        // Navigation Drawer
        this.mAppBarConfig = AppBarConfiguration(
            setOf(R.id.nav_videos, R.id.nav_playlists),
            this.main_activity_drawerlayout
        )
        val navController = findNavController(R.id.main_content_hostfrag)
        setupActionBarWithNavController(navController, this.mAppBarConfig)
        this.main_activity_navigationview.setupWithNavController(navController)
    }

    /************************************************************************
     * Purpose:         Set Navigation Header
     * Precondition:    Whenever the user chooses to navigate Up within your
     *                  application's activity hierarchy from the action bar
     * Postcondition:   Return navigation controller
     ************************************************************************/
    private fun setNavHeader() {
        // TODO Navigation Drawer To Bottom Navigation Layout
        // TODO OR Adapt The Drawer To Tablet
        this.mAuthInstance = FirebaseAuth.getInstance()

        this.main_activity_navigationview
            .getHeaderView(0)
            .findViewById<TextView>(R.id.Main_Navheader_UserName)
            .text = this.mAuthInstance.currentUser?.displayName

        this.main_activity_navigationview
            .getHeaderView(0)
            .findViewById<TextView>(R.id.Main_Navheader_UserEmail)
            .text = this.mAuthInstance.currentUser?.email
    }

    /************************************************************************
     * Purpose:         On Support Navigate Up
     * Precondition:    Whenever the user chooses to navigate Up within your
     *                  application's activity hierarchy from the action bar
     * Postcondition:   Return navigation controller
     ************************************************************************/
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_content_hostfrag)
        return navController.navigateUp(this.mAppBarConfig) || super.onSupportNavigateUp()
    }

    /************************************************************************
     * Purpose:         Localization
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    override fun attachBaseContext(newBase : Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase!!))
    }

    override fun onResume() {
        super.onResume()
        if (mInitLocale != LocaleHelper.getPersistedLocale(this)) {
            recreate()
        }
    }
}