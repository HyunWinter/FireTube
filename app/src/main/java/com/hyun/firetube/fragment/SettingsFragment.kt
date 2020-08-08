package com.hyun.firetube.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import com.hyun.firetube.`interface`.AuthActivity


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

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        this.googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        this.authInstance = FirebaseAuth.getInstance()

        findPreference<Preference>(getString(R.string.Settings_Logout_Key))?.setOnPreferenceClickListener {

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

        // Get Themes
        val themeList = resources.getStringArray(R.array.Settings_Theme_Alias)

        // Preference Listener
        findPreference<ListPreference>(getString(R.string.Settings_Theme_Key))?.setOnPreferenceChangeListener {
                preference, newValue ->

            when (newValue) {
                // Light Theme
                themeList[0] -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                // Dark Theme
                themeList[1] -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                // System Theme
                themeList[2] -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }

            true
        }
    }

    private fun setLanguage() {
        // Get Themes
        val langList = resources.getStringArray(R.array.Settings_Language_Alias)

        // Preference Listener
        findPreference<ListPreference>(getString(R.string.Settings_Language_Key))?.setOnPreferenceChangeListener {
                preference, newValue ->

            when (newValue) {
                // en
                langList[0] -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            true
        }
    }

    // About
    private fun setAbout() {

        findPreference<Preference>(getString(R.string.Settings_Version_Key))?.summary = BuildConfig.VERSION_NAME
        findPreference<Preference>(getString(R.string.Settings_Policy_Key))?.setOnPreferenceClickListener {

            true
        }
        findPreference<Preference>(getString(R.string.Settings_Terms_Key))?.setOnPreferenceClickListener {

            true
        }
    }
}