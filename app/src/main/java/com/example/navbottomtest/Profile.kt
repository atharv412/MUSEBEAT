package com.example.navbottomtest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

        val userName = findViewById<TextView>(R.id.name)
//        val userLastname = findViewById<EditText>(R.id.last_name)
        val userEmail = findViewById<TextView>(R.id.email)
        val userCountry = findViewById<TextView>(R.id.country)
        val back=findViewById<ImageButton>(R.id.backButton)
        val editBtn=findViewById<Button>(R.id.edit_profile_btn)
        var userId:Int?=0


        CoroutineScope(Dispatchers.IO).launch {

            user = session.auth.retrieveUserForCurrentSession(updateSession = true)
            val u_email = user.email
            val db_user = session.from("user").select{
                filter {
                    eq("user_email", u_email.toString())
                }
            }.decodeSingleOrNull<UserModel>()

            val currentUser=session.from("user").select { filter { eq("user_email", u_email.toString()) } }
            println("Current user from outer profile"+currentUser.data)

            withContext(Dispatchers.Main) {
                println(db_user)
                if (user.email.isNullOrEmpty())
                {
                    val intent= Intent(this@Profile,Login::class.java)
                    startActivity(intent)
                    finish()
                }
                else if(db_user != null) {
//                    userFirstname.setText(db_user.user_firstname)
//                    userLastname.setText(db_user.user_lastname)
                    userName.text=db_user.user_firstname+" "+db_user.user_lastname
                    userEmail.text=db_user.user_email
                    userCountry.text=db_user.user_country
                    userId=db_user.id
                }
            }
        }

        back.setOnClickListener{
            finish()
        }

        editBtn.setOnClickListener {
            val intent=Intent(this@Profile,EditProfile::class.java)
            intent.putExtra("id",userEmail.text)
            intent.putExtra("userName",userName.text)
//            intent.putExtra("userMobileNo",userName.text)
            intent.putExtra("userCountry",userCountry.text)
            startActivity(intent)

        }
    }
}