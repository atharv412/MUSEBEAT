package com.example.navbottomtest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Welcome : AppCompatActivity() {
    private val user=SupabaseClientProvider.client
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)


//        CoroutineScope(Dispatchers.IO).launch {
//            val loginStatus=user.auth.currentSessionOrNull()
//
//            withContext(Dispatchers.Main){
//                if (loginStatus != null) {
//                        val intent=Intent(this@Welcome,Login::class.java)
//                        startActivity(intent)
//                        finish()
//                }
//            }
//        }

        val registerButton=findViewById<Button>(R.id.join_us)
        registerButton.setOnClickListener {
            val intent= Intent(this,Register::class.java)
            startActivity(intent)
        }

        val loginButton=findViewById<Button>(R.id.login)
        loginButton.setOnClickListener {
            val intent=Intent(this,Login::class.java)
            startActivity(intent)
        }
    }
}