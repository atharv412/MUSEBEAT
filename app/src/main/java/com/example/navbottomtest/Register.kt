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
import com.example.navbottomtest.models.UserModel
import com.google.android.material.textfield.TextInputEditText // Make sure this import is included
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
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
        val user=findViewById<TextInputEditText>(R.id.user_name)

//        val textview=findViewById<TextView>(R.id.loginNow)

//        textview.setOnClickListener{
//            val intent=Intent(this,Login::class.java)
//            startActivity(intent)
//
//        }

        registerButton.setOnClickListener {
            // Validate if passwords match
            if (setPassword.text.toString() == confirmPassword) {
                Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.IO).launch {
                // Show the progress indicator on the main thread
                withContext(Dispatchers.Main) {
                    waiting.visibility = View.VISIBLE
                }
                try {
                    // Sign up the user with email and password
                    var user:UserInfo?=null

                    val db_user=client.from("user").select(columns = Columns.list("user_email")) {
                        filter{
                            eq("user_email",setEmail.text.toString())
                        }
                    }
                    println(db_user.data.length)
                    println(db_user.data)

                    // TODO: check the authException (Supabase is returning an empty list ([]))//completed
//                    if ( db_user.data==null||db_user.data.isEmpty())
                        if ( db_user.data.length==2)

                    {

                        println("No user found, proceeding with sign-up...")
                        user = client.auth.signUpWith(Email) {
                            email=setEmail.text.toString()
                            password = setPassword.text.toString()
                        }
                        val create_user=UserModel(user_firstname = "", user_lastname = "", user_email =setEmail.text.toString(), user_history = mutableListOf() ,user_profile_photo="", user_country = "")
                        client.from("user").insert(create_user)
                        println("User registered successfully!")
                    }else{
                        // TODO: check the authException (Supabase is returning an empty list ([]))

                        Log.d("atharva","User already exists")
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@Register, "User with given Email already exists redirecting to login page", Toast.LENGTH_LONG).show()
                        }
                    }

                    withContext(Dispatchers.Main) {
                        if (user == null) {
//                            // Navigate to login screen on success
                            waiting.visibility = View.GONE
//                            Toast.makeText(this@Register, "redirecting to login page", Toast.LENGTH_LONG).show()
//                            println("redirecting to login page")
                            val intent = Intent(this@Register, Login::class.java)
                            startActivity(intent)
                            finish()
                        }else if(user!=null){
                            waiting.visibility = View.GONE
                            Toast.makeText(this@Register, "Sign up Successfully", Toast.LENGTH_LONG).show()
                            println("redirecting to login page")
                            val intent = Intent(this@Register, Login::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else {
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
        // TODO: complete profile creation and db connectivity//completed
        // TODO: start music player creation//completed
    }
}