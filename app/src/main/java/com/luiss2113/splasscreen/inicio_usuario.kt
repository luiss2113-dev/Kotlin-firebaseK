package com.luiss2113.splasscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_inicio_usuario.*

class inicio_usuario : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_usuario)

        val user = FirebaseAuth.getInstance()
        datosUser()
        mAuth = FirebaseAuth.getInstance()

        singOut.setOnClickListener {
            LayoutMain(intent)
        }
    }

    fun LayoutMain(intent: Intent){
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        signOutUser()
    }

    private fun signOutUser() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut()

    }

    private fun datosUser(){
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            UserSesionText.text = email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified
            println("la sesión es $email y $emailVerified")

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
            print(" el uid es: $uid")
        }
    }

    private fun updateUI(user: FirebaseUser?) {
    }

}
