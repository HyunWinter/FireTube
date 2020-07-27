package com.hyun.firetube.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.hyun.firetube.R
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_appbar.*
import kotlinx.android.synthetic.main.main_content.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAppBarConfig : AppBarConfiguration
    private lateinit var mDrawerLayout : DrawerLayout

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

    private fun setContents() {
        setSupportActionBar(this.main_appbar_toolbar)

        this.mAppBarConfig = AppBarConfiguration(
            setOf(R.id.nav_video, R.id.nav_playlist),
            this.main_activity_drawerlayout
        )
        val navController = findNavController(R.id.main_content_hostfrag)
        setupActionBarWithNavController(navController, mAppBarConfig)
        this.main_activity_navigationview.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_content_hostfrag)
        return navController.navigateUp(mAppBarConfig) || super.onSupportNavigateUp()
    }
}