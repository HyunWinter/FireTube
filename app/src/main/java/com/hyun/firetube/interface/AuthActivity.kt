package com.hyun.firetube.`interface`

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.hyun.firetube.R
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : BaseActivity() {

    companion object {
        private const val TAG = "AuthActivity"  // Logcat
        private const val RC_SIGN_IN = 9001     // Google Auth
        private const val FadeDuration = 4000   // BG Animation
    }

    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private lateinit var mAuthInstance : FirebaseAuth
    private lateinit var mAuthListener : AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        this.setThemePreference()
        this.setLanguagePreference()
        this.setFirebaseAuth()
        this.setGoogleAuth()
    }

    /************************************************************************
     * Purpose:         Set Default Preference
     * Precondition:    onCreate
     * Postcondition:   Call user preferences if exist
     ************************************************************************/
    private fun setThemePreference() {

        // Get Themes
        val themeList = resources.getStringArray(R.array.Settings_Theme_Alias)

        // Load Shared Preferences
        val savedTheme = PreferenceManager
            .getDefaultSharedPreferences(this)
            .getString(getString(R.string.Settings_Theme_Key), themeList[0])

        // Change Theme
        when (savedTheme) {
            themeList[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            themeList[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            themeList[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setLanguagePreference() {

        // Get Themes
        val langList = resources.getStringArray(R.array.Settings_Language_Alias)

        // Load Shared Preferences
        val savedLang = PreferenceManager
            .getDefaultSharedPreferences(this)
            .getString(getString(R.string.Settings_Language_Key), langList[0])

        // Change Theme
        when (savedLang) {
            /*langList[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            langList[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            langList[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)*/
        }
    }

    /************************************************************************
     * Purpose:         Firebase Authentication Session Management
     * Precondition:    Authentication State Changed
     * Postcondition:   Move to Main Activity when Logged in
     ************************************************************************/
    private fun setFirebaseAuth() {

        this.mAuthInstance = FirebaseAuth.getInstance()
        this.mAuthListener = AuthStateListener { firebaseAuth ->

            val user = firebaseAuth.currentUser

            if (user != null) {
                hideProgressBar(Auth_ProgressBar)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Log.d(TAG, "onAuthStateChanged: signed_out")
                this.showLoginLayout()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        this.mAuthInstance.addAuthStateListener(this.mAuthListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mAuthInstance.removeAuthStateListener(this.mAuthListener)
    }

    /************************************************************************
     * Purpose:         Google Authentication
     * Precondition:    Google Login Button Clicked
     * Postcondition:   onActivityResult() ->
     *                  firebaseAuthWithGoogle() ->
     *                  AuthStateListener in setFirebaseAuth()
     ************************************************************************/
    private fun setGoogleAuth() {

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestScopes(Scope(YouTubeScopes.YOUTUBE_READONLY))
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        this.mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        Auth_GoogleLogIn.setOnClickListener{
            showProgressBar(Auth_ProgressBar)
            val signInIntent = this.mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            if (task.isSuccessful) {
                val account = task.getResult(ApiException::class.java)!!

                Log.w(TAG, "Google log in success: " + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            }
            else {
                Log.w(TAG, "Google log in failed: ", task.exception)
                makeSnackBar(this.Auth_Background, getString(R.string.Auth_GoogleLoginFailed))
                hideProgressBar(Auth_ProgressBar)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        this.mAuthInstance
            .signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    //val user = authInstance.currentUser
                }
                else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    makeSnackBar(this.Auth_Background, getString(R.string.Auth_FirebaseLoginFailed))
                    hideProgressBar(Auth_ProgressBar)
                }
            }
    }

    /************************************************************************
     * Purpose:         Show & Hide XML
     * Precondition:    .
     * Postcondition:   Load Layouts When Logged Out
     ************************************************************************/
    private fun hideLoginLayout()
    {
        this.Auth_LoginLayout.visibility = View.GONE
        this.Auth_LoginLayout.isClickable = false
        this.showProgressBar(this.Auth_ProgressBar)
    }

    private fun showLoginLayout()
    {
        this.Auth_LoginLayout.visibility = View.VISIBLE
        this.Auth_LoginLayout.isClickable = true
        this.hideProgressBar(this.Auth_ProgressBar)
        this.startBGAnim()
    }

    /************************************************************************
     * Purpose:         Background
     * Precondition:    .
     * Postcondition:   Start Background Animation
     ************************************************************************/
    private fun startBGAnim() {

        val animationDrawable = Auth_Background.background as AnimationDrawable

        animationDrawable.apply {
            setEnterFadeDuration(FadeDuration)
            setExitFadeDuration(FadeDuration)
            start()
        }
    }
}