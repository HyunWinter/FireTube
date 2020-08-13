package com.hyun.firetube.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.hyun.firetube.BuildConfig
import com.hyun.firetube.R
import com.hyun.firetube.activity.AuthActivity
import com.hyun.firetube.database.PlaylistDB


class SettingsFragment : PreferenceFragmentCompat() {

    // Companion
    companion object {
        private const val TAG = "SettingsFragment"  // Logcat
    }

    private lateinit var authInstance : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreatePreferences(savedInstanceState : Bundle?, rootKey : String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)

        this.setLogout()
        this.setTheme()
        this.setLanguage()
        this.setAbout()
    }

    // Account
    private fun setLogout() {

        findPreference<Preference>(getString(R.string.Settings_Logout_Key))?.setOnPreferenceClickListener {

            // Database
            val playlistsDB = PlaylistDB(requireActivity())
            playlistsDB.clearDatabase(requireActivity())

            // Logout from Both Google and Firebase
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            this.googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            this.authInstance = FirebaseAuth.getInstance()

            this.googleSignInClient
                .signOut()
                .addOnSuccessListener {
                    this.authInstance.signOut()
                    val intent = Intent(activity, AuthActivity::class.java)
                    startActivity(intent)
                }
            true
        }
    }

    // General
    private fun setTheme() {

        // Preference Listener
        findPreference<ListPreference>(getString(R.string.Settings_Theme_Key))?.setOnPreferenceChangeListener {
                _, newValue ->

            // Theme Mode
            val themeList = resources.getStringArray(R.array.Settings_Theme_Alias)

            when (newValue) {
                themeList[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                themeList[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                themeList[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

            true
        }
    }

    private fun setLanguage() {

        // Preference Listener
        findPreference<ListPreference>(getString(R.string.Settings_Language_Key))?.setOnPreferenceChangeListener {
                _, newValue ->

            //val langList = resources.getStringArray(R.array.Settings_Language_Alias)

            /*when (newValue) {
                // en
                langList[0] -> ""
            }*/

            true
        }
    }
    
    // About
    private fun setAbout() {

        findPreference<Preference>(getString(R.string.Settings_Version_Key))?.summary = BuildConfig.VERSION_NAME
        findPreference<Preference>(getString(R.string.Settings_Policy_Key))?.setOnPreferenceClickListener {

            val webView = WebView(requireActivity())
            webView.loadUrl("file:///android_asset/PrivacyPolicy.html")

            AlertDialog.Builder(requireActivity(), R.style.DialogStyle)
                .setView(webView)
                .setPositiveButton("OK") { _, _ ->
                    // Do Nothing
                }
                .show()

            true
        }
        findPreference<Preference>(getString(R.string.Settings_Terms_Key))?.setOnPreferenceClickListener {

            val webView = WebView(requireActivity())
            webView.loadUrl("file:///android_asset/TermsAndConditions.html")

            AlertDialog.Builder(requireActivity(), R.style.DialogStyle)
                .setView(webView)
                .setPositiveButton("OK") { _, _ ->
                    // Do Nothing
                }
                .show()

            true
        }
    }
}