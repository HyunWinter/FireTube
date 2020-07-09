package com.hyun.firetube.auth

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
import com.hyun.firetube.ui.MainActivity
import com.hyun.firetube.utility.BaseActivity
import kotlinx.android.synthetic.main.auth_main.*

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
        setContentView(R.layout.auth_main)

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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Log.d(TAG, "onAuthStateChanged: signed_out")
                //makeSnackbar(this.Auth_Background, getString(R.string.Auth_SignedOutText))
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
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        this.googleSignInClient = GoogleSignIn.getClient(this, gso)

        Auth_GoogleLogIn.setOnClickListener{
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            if (task.isSuccessful) {
                val account = task.getResult(ApiException::class.java)!!
                Log.w(TAG, "Google sign in success: " + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            }
            else {
                Log.w(TAG, "Google sign in failed: ", task.exception)
                makeSnackbar(this.Auth_Background, task.exception.toString())
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
                    val user = authInstance.currentUser
                }
                else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    makeSnackbar(this.Auth_Background, getString(R.string.Auth_FirebaseLoginFailed))
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
        this.showProgressBar(this.Auth_ProgressBar)
    }

    private fun showLoginLayout()
    {
        this.Auth_LoginLayout.visibility = View.VISIBLE
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