package com.hyun.firetube.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.hyun.firetube.BuildConfig
import com.hyun.firetube.R

class SettingsFragment : PreferenceFragmentCompat() {

    // Companion
    companion object {
        private const val TAG = "SettingsFragment"  // Logcat
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)

        this.setAppTheme()
        this.setLanguage()
        this.setAbout()
    }

    private fun setAppTheme() {
        findPreference<Preference>(getString(R.string.Settings_Theme_Key))?.setDefaultValue(1)
    }

    private fun setLanguage() {
        findPreference<Preference>(getString(R.string.Settings_Language_Key))?.setDefaultValue(1)
    }

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