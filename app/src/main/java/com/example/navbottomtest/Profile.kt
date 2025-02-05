package com.example.navbottomtest

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.navbottomtest.models.UserModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Profile: AppCompatActivity() {
    private  lateinit var user: UserInfo
    private val session = SupabaseClientProvider.client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_proflie)

        val userFirstname = findViewById<EditText>(R.id.first_name)
        val userLastname = findViewById<EditText>(R.id.last_name)
        val userEmail = findViewById<EditText>(R.id.email)
        val userCountry = findViewById<EditText>(R.id.country)

        CoroutineScope(Dispatchers.IO).launch {

            user = session.auth.retrieveUserForCurrentSession(updateSession = true)
            val u_email = user.email
            val db_user = session.from("user").select() {
                filter {
                    eq("user_email", u_email.toString())
                }
            }.decodeSingleOrNull<UserModel>()
            withContext(Dispatchers.Main) {
                println(db_user)
                if (user.email.isNullOrEmpty())
                {
                    val intent= Intent(this@Profile,Login::class.java)
                    startActivity(intent)
                    finish()
                }
                else if(db_user != null) {
                    userFirstname.setText(db_user.user_firstname)
                    userLastname.setText(db_user.user_lastname)
                    userEmail.setText(db_user.user_email)
                    userCountry.setText(db_user.user_country)
                }
            }
        }
    }
}