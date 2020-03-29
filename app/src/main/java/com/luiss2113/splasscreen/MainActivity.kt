package com.luiss2113.splasscreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
   private lateinit var googleSignInClient: GoogleSignInClient

    //función de app arrancando
    override fun onCreate(savedInstanceState: Bundle?) {
        //VALIDA SI HAY SESIONES
        checkCurrentUser()
        //indicar la muestra del nuevo tema
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure Google Sign In -> PARTE 1
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        println("${R.string.default_web_client_id} ESTO ES LO QUE TRAE ESTE DATO" )

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]

        //instaciando firebase
        mAuth = FirebaseAuth.getInstance()

        btnRegistroLayout.setOnClickListener {
            Toast.makeText(this, "redireccionando.", Toast.LENGTH_SHORT).show()
            //llamar al layout
            LayoutRegistro(intent)
            }


        btnIniciarSesion.setOnClickListener {
            val emailUserForm = emailUser.text.toString().trim()
            val passRegistroForm = passUser.text.toString().trim()
            validarCamposLogin(emailUserForm, passRegistroForm)
        }

        //CLICK EN EL BOTÓN DE GOOGL
        btnGoogleAuth.setOnClickListener(){
            signIn()
        }
    }

    private fun LayoutRegistro(intent: Intent){
        val intent: Intent = Intent(this, registro_usuarios_nuevos::class.java)
        startActivity(intent)
    }

     fun LayoutMain(intent: Intent){
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun LayoutUsuario(intent: Intent){
        val intent: Intent = Intent(this, inicio_usuario::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "EmailPassword"

        //CONSTANTE DE GOOGLE
        private const val RC_SIGN_IN = 9001
        private const val TAN = "GoogleActivity"
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        println("curren user: $currentUser")
        updateUI(currentUser)
    }

    private fun validarCamposLogin(emailUserForm:String , passRegistroForm:String){
        if (emailUserForm.isNullOrEmpty() or passRegistroForm.isNullOrEmpty()){
            Toast.makeText(this, "verifique los campos.", Toast.LENGTH_SHORT).show()
        }else {
            inicioSesion(emailUserForm, passRegistroForm)
            Toast.makeText(this, "Redireccioando a inicio de sesión", Toast.LENGTH_SHORT).show()
        }
    }

    fun inicioSesion(emailUserForm: String, passwordUserForm: String) {
        mAuth.signInWithEmailAndPassword(emailUserForm, passwordUserForm)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    LayoutUsuario(intent)
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception?.cause)
                    Toast.makeText(
                        baseContext, "Fallo de autenticación, Verifique sus datos.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }

                // ...
            }
    }


    private fun updateUI(user: FirebaseUser?) {
    }

    //INICIAR SESIÓN CON GOOGLE
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAN, "Google sign in failed", e.cause)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAN, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAN, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    LayoutUsuario(intent)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAN, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
                // ...
            }
    }
    //

    private fun signOut() {
        // Firebase sign out
        mAuth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
        }
    }



    private fun checkCurrentUser() {
        // [START check_current_user]
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            LayoutUsuario(intent)
        } else {
            print("NO HAY SESIONES")
            // No user is signed in
        }
    }
        // [END check_current_user]
    }

