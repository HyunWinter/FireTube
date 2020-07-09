package com.hyun.firetube.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.hyun.firetube.R
import com.hyun.firetube.auth.AuthActivity
import com.hyun.firetube.utility.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var authInstance : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setDatabase()
        this.setHeaderText()
    }

    private fun setDatabase() {

        this.authInstance = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestScopes(Scope(YouTubeScopes.YOUTUBE_READONLY))
            //.requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        this.googleSignInClient = GoogleSignIn.getClient(this, gso)

        Auth_SignOut.setOnClickListener {

            this.googleSignInClient
                .signOut()
                .addOnSuccessListener {
                    this.authInstance.signOut()
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        }
    }

    private fun setHeaderText() {

        val profileName = this.authInstance.currentUser?.displayName
        val successText = profileName + Auth_MainPageText.text
        Auth_MainPageText.text = successText
    }
}