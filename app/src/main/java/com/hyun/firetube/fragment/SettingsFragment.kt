package com.hyun.firetube.fragment

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)

        this.setLogout()
        this.setAppTheme()
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
    private fun setAppTheme() {
        findPreference<Preference>(getString(R.string.Settings_Theme_Key))?.setDefaultValue(1)
    }

    private fun setLanguage() {
        findPreference<Preference>(getString(R.string.Settings_Language_Key))?.setDefaultValue(1)
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