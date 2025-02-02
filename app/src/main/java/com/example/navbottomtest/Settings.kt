package com.example.navbottomtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Settings:AppCompatActivity() {

    private  lateinit var user:UserInfo
    private val session = SupabaseClientProvider.client.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val userDetails=findViewById<TextView>(R.id.userProfile)
        val logout=findViewById<Button>(R.id.logout)
        val about=findViewById<Button>(R.id.aboutBtn)
        val back=findViewById<ImageButton>(R.id.backButton)

        CoroutineScope(Dispatchers.IO).launch {
            user = session.retrieveUserForCurrentSession(updateSession = true)

            withContext(Dispatchers.Main) {
                if (user.email.isNullOrEmpty())
                {
                    val intent= Intent(this@Settings,Login::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    userDetails.text=user.email
                }
            }
        }

        back.setOnClickListener{
            finish()
        }

        logout.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                session.signOut()

                withContext(Dispatchers.Main){
                    Toast.makeText(this@Settings,"Logout successfull", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Settings, Welcome::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
        about.setOnClickListener {
            val intent = Intent(this@Settings, About::class.java)
            startActivity(intent)
        }
    }
}