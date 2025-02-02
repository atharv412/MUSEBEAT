package com.example.navbottomtest

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText // Make sure this import is included
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Register : AppCompatActivity() {
    private val client=SupabaseClientProvider.client


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register2)


        val registerButton=findViewById<Button>(R.id.registerButton)
        val waiting=findViewById<ProgressBar>(R.id.progressBar)
        val setEmail=findViewById<TextInputEditText>(R.id.email)
        val setPassword=findViewById<TextInputEditText>(R.id.password)
        val confirmPassword=findViewById<TextInputEditText>(R.id.confirmPassword).text.toString()

//        val textview=findViewById<TextView>(R.id.loginNow)

//        textview.setOnClickListener{
//            val intent=Intent(this,Login::class.java)
//            startActivity(intent)
//
//        }

        registerButton.setOnClickListener {
            // Validate if passwords match
//            if (setPassword.text.toString() != confirmPassword) {
//                Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            }
            CoroutineScope(Dispatchers.IO).launch {
                // Show the progress indicator on the main thread
                withContext(Dispatchers.Main) {
                    waiting.visibility = View.VISIBLE
                }
                try {
                    // Sign up the user with email and password
                    val user = client.auth.signUpWith(Email) {

                        email=setEmail.text.toString()
                        password = setPassword.text.toString()
                    }

                    withContext(Dispatchers.Main) {
                        if (user == null) {
                            // Navigate to login screen on success
                            waiting.visibility = View.GONE
                            Toast.makeText(this@Register, "Sign-up Successfully", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@Register, Login::class.java)

                            startActivity(intent)
                            finish()
                        } else {
                            // Notify user of failure
                            waiting.visibility = View.GONE
                            Toast.makeText(this@Register, "Sign-up failed. Please try again.", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    // Handle any exceptions during sign-up
                    withContext(Dispatchers.Main) {
                        waiting.visibility = View.GONE
                        Toast.makeText(this@Register, "Error: ${e.message} ${e.cause}", Toast.LENGTH_LONG).show()
                        Log.d("atharva error","error",e)
                    }
                }
            }
        }


        // TODO: complete profile creation and db connectivity
        // TODO: start music player creation


//        registerButton.setOnClickListener {
//            if(setPassword!=confirmPassword)
//            {
//                Toast.makeText(this,"Password and Confirm Password does not match",Toast.LENGTH_LONG).show()
//            }
//            CoroutineScope(Dispatchers.IO).launch{
//
////                waiting.visibility=View.VISIBLE
//                val user = client.auth.signUpWith(Email) {
//                    email=setEmail
//                    password=setPassword
//                }
//                if(user!=null){
//
//                    withContext(Dispatchers.Main){
//                        waiting.visibility=View.GONE
//                        val intent=Intent(this@Register,Login::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                }
//                else{//gives a toast message to the user
//                    withContext(Dispatchers.Main){
//
//                        Toast.makeText(this@Register,"Check password & confirmPassword",Toast.LENGTH_LONG).show()
//                    }
////                    waiting.visibility=View.GONE
//                }
//            }
////            Toast.makeText(this@Register,"account created successfully",Toast.LENGTH_LONG).show()
//        }




    }
}