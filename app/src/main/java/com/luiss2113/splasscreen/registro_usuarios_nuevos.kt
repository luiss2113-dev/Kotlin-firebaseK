package com.luiss2113.splasscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_registro_usuarios_nuevos.*


class registro_usuarios_nuevos : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuarios_nuevos)

        //instaciando firebase
        mAuth = FirebaseAuth.getInstance()

        btnRegistroNew.setOnClickListener() {
            val emailRegistroU = registroEmailUser.text.toString().trim()
            val passRegistroU = registroPassUser.text.toString().trim()
            validarCampos(emailRegistroU, passRegistroU)
        }

        InicioSesionUser.setOnClickListener {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    private fun LayoutRegistro(intent: Intent){
        val intent: Intent = Intent(this, registro_usuarios_nuevos::class.java)
        startActivity(intent)
        finish()
    }

    private fun LayoutMain(intent: Intent){

    }

    private fun LayoutUsuario(intent: Intent){
        val intent: Intent = Intent(this, inicio_usuario::class.java)
        startActivity(intent)
        finish()
    }


    fun validarCampos(emailRegistroU:String , passRegistroU:String){
        if (emailRegistroU.isNullOrEmpty() or passRegistroU.isNullOrEmpty()){
            Toast.makeText(this, "verifique los campos.", Toast.LENGTH_SHORT).show()
        }else {
            crearUsuario(emailRegistroU, passRegistroU)
        }
    }

    fun crearUsuario(emailText: String, passText: String) {
        mAuth.createUserWithEmailAndPassword(emailText, passText)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                    LayoutUsuario(intent)
                    Toast.makeText(this, "Se ha guardado tu información", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "No se puede registrar este correo electrónico.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null){
        println("sesiín null")
        }else{
            LayoutUsuario(intent)
            println("los datos a traer son $user")
            }
        }
    }


