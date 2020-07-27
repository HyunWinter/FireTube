package com.hyun.firetube.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.hyun.firetube.R
import kotlinx.android.synthetic.main.auth_activity.*

class AuthActivity : BaseActivity() {

    companion object {
        private const val TAG = "AuthActivity"  // Logcat
        private const val RC_SIGN_IN = 9001     // Google Auth
        private const val FadeDuration = 4000   // BG Animation
    }

    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var authInstance : FirebaseAuth
    private lateinit var authListener : AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)

        this.hideLoginLayout()
        this.setFirebaseAuth()
        this.setGoogleAuth()
    }

    /************************************************************************
     * Purpose:         Firebase Authentication Session Management
     * Precondition:    Authentication State Changed
     * Postcondition:   Move to Main Activity when Logged in
     ************************************************************************/
    private fun setFirebaseAuth() {

        this.authInstance = FirebaseAuth.getInstance()
        this.authListener = AuthStateListener { firebaseAuth ->

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
        this.authInstance.addAuthStateListener(this.authListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.authInstance.removeAuthStateListener(this.authListener)
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

        this.googleSignInClient = GoogleSignIn.getClient(this, gso)

        Auth_GoogleLogIn.setOnClickListener{
            showProgressBar(Auth_ProgressBar)
            val signInIntent = this.googleSignInClient.signInIntent
            startActivityForResult(signInIntent,
                RC_SIGN_IN
            )
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
        this.authInstance
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
        this.Auth_LoginLayout.isEnabled = false
        this.showProgressBar(this.Auth_ProgressBar)
    }

    private fun showLoginLayout()
    {
        this.Auth_LoginLayout.visibility = View.VISIBLE
        this.Auth_LoginLayout.isEnabled = true
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