package com.hyun.firetube.`interface`

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hyun.firetube.R
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_appbar.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAppBarConfig : AppBarConfiguration

    /************************************************************************
     * Purpose:         onCreate
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        this.setContents()
    }

    /************************************************************************
     * Purpose:         Set Contents
     * Precondition:    .
     * Postcondition:   Initialize default contents
     ************************************************************************/
    private fun setContents() {
        setSupportActionBar(this.main_appbar_toolbar)

        this.mAppBarConfig = AppBarConfiguration(
            setOf(R.id.nav_videos, R.id.nav_playlists, R.id.nav_settings),
            this.main_activity_drawerlayout
        )
        val navController = findNavController(R.id.main_content_hostfrag)
        setupActionBarWithNavController(navController, mAppBarConfig)
        this.main_activity_navigationview.setupWithNavController(navController)
    }

    /************************************************************************
     * Purpose:         On Create Options Menu
     * Precondition:    When menu is constructed
     * Postcondition:   Inflate menu items
     ************************************************************************/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /************************************************************************
     * Purpose:         On Support Navigate Up
     * Precondition:    Whenever the user chooses to navigate Up within your
     *                  application's activity hierarchy from the action bar
     * Postcondition:   Return navigation controller
     ************************************************************************/
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_content_hostfrag)
        return navController.navigateUp(mAppBarConfig) || super.onSupportNavigateUp()
    }
}