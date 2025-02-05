package com.example.navbottomtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {
    private val client=SupabaseClientProvider.client

    public override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            val currentUser=client.auth.currentSessionOrNull()

            withContext(Dispatchers.Main){
                if (currentUser != null) {
                val intent=Intent(this@Login,MainActivity::class.java)
                startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val setEmail = findViewById<TextInputEditText>(R.id.email)
        val setPassword = findViewById<TextInputEditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val textview = findViewById<TextView>(R.id.registerNow)
        val waiting = findViewById<ProgressBar>(R.id.progressBar)

        textview.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                var user: Unit
                try {
                    val user1 = setEmail.text.toString()
                    val password1 = setPassword.text.toString()
//                waiting.visibility=View.VISIBLE
                    user = client.auth.signInWith(Email) {//logins the user and send the user details back
                            email = user1
                            password = password1
                        }
                    if (user != null) {//checks if the login is successful if yes move to mainActivity else
                        withContext(Dispatchers.Main) {
//                        waiting.visibility=View.GONE
                            val intent = Intent(this@Login, MainActivity::class.java)
                            Toast.makeText(this@Login, "Login Successful", Toast.LENGTH_LONG).show()
                            startActivity(intent)
                            finish()
                        }
                    }
                } catch (e: AuthRestException) {
                    println("Error: ${e.message}")

                    if (e.message?.contains("Invalid login credentials") == true) {
                        println("Incorrect email or password. Please try again.")
                        withContext(Dispatchers.Main) {
                            waiting.visibility = View.GONE
                            Toast.makeText(
                                this@Login,
                                "Login Unsuccessful, check credentials",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        println("Authentication error: ${e.message}")
                    }

                }
            }
        }
    }
}