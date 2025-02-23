package com.example.navbottomtest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.navbottomtest.models.UserModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.CharacterData

class EditProfile:AppCompatActivity() {
    private val session = SupabaseClientProvider.client
    lateinit var prevUserFirstName:String
    lateinit var prevUserLastName:String
    lateinit var prevUserCountry:String
    lateinit var currentUser:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val userFirstName=findViewById<EditText>(R.id.first_name)
        val userLastName=findViewById<EditText>(R.id.last_name)
        val userMobileNo=findViewById<EditText>(R.id.mobile_number)
        val userCountry=findViewById<EditText>(R.id.country)

        val back=findViewById<ImageButton>(R.id.backButton)
        val updateBtn=findViewById<Button>(R.id.update_profile_button)



//        val tempuser = intent.getStringExtra("userName") ?: "Default Value"
//        val tempcountry = intent.getStringExtra("userCountry")
//        val emailid = intent.getStringExtra("id")?:"default"
//
////        val tempUserId=intent.getLongExtra("id",0L)
//        println(emailid)
//
//        val tempUserName=tempuser.split(" ")
//        userFirstName.setText(tempUserName[0])
//        userLastName.setText(tempUserName[1])
//        userCountry.setText(tempcountry)
        getPreviousValues()
            //setting default values from the previous activity

        back.setOnClickListener{
            finish()
        }
        updateBtn.setOnClickListener {
            val newFirstName = userFirstName.text.toString().trim()
            val newLastName = userLastName.text.toString().trim()
            val newCountry = userCountry.text.toString().trim()

            val originalFirstName = prevUserFirstName
            val originalLastName = prevUserLastName
            val originalCountry = prevUserCountry

            if (newFirstName != originalFirstName || newLastName != originalLastName ||
                newCountry != originalCountry)
            {

                println("Updating: $newFirstName $newLastName (Email: $currentUser) $newCountry")
                CoroutineScope(Dispatchers.IO).launch {
                    // 1️⃣ Check if user exists
//                    val existingUser = session.from("user").select {
//                        filter {
//                            eq("user_email", currentUser)
//                        }
//                    }.decodeSingleOrNull<UserModel>() // ✅ Fix crash if user doesn't exist
                    val currentUser1=session.from("user").select { filter { eq("user_email",currentUser) } }.decodeSingle<UserModel>()
                    println("current user from inner profile"+currentUser1.user_email)

                    if (currentUser1 == null) {
                        println("User with email $currentUser1 does not exist. Update failed!")
                        return@launch
                    }
//                    println("current user while updating"+currentUser)
                    println("User exists: "+currentUser1.user_email)

                    // 2️⃣ Perform update query

                    val result=session.from("user").update(
                        mapOf(
                            "user_firstname" to newFirstName,
                            "user_lastname" to newLastName,
                            "user_country" to newCountry
                        )
//                        {
//                            set("user_firstname",newFirstName)
//                            set("user_lastname",newLastName)
//                            set("user_country",newCountry)
//                        }
                    ){
                        select()
                        filter {
//                            Character::user_email eq currentUser
                            eq("user_email",currentUser)
                        }
                    }.decodeSingleOrNull<UserModel>()


                    //version2
                    val result2=session.from("user").update{
                        mapOf(
                            "user_firstname" to userFirstName,
                            "user_lastname" to userLastName,
                            "user_country" to userCountry
                        )
                        select()
                        filter {
                            eq("user_email",currentUser1.user_email)
                        }
                    }.decodeSingleOrNull<UserModel>()
                    println("updated username"+result2?.user_firstname)

                    //todo i genuinely dont know whats wrong with the above  query


//                    println("User exists: firsname: ${result.user_firstname} and id: ${result.id}")
                    println(result)
                    withContext(Dispatchers.Main) {
                        if (result?.toString()?.isNotEmpty() == true) {
                            Toast.makeText(this@EditProfile, "Profile updated successfully", Toast.LENGTH_LONG).show()
                            println("Updated successfully: ${result.user_firstname}")
                        } else {
                            Toast.makeText(this@EditProfile, "Update failed!", Toast.LENGTH_LONG).show()
                            println("Update failed!")
                        }
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
// TODO: highly unstable the update query runs but doesnt update anything returning error when tried destructuring
    //todo  kotlinx.serialization.json.internal.JsonDecodingException: Expected start of the array '[', but had 'EOF' instead at path: $



//        updateBtn.setOnClickListener {
//            val newFirstName = userFirstName.text.toString().trim()
//            val newLastName = userLastName.text.toString().trim()
////            val newMobileNo = userMobileNo.text.toString().trim()
//            val newCountry = userCountry.text.toString().trim()
//                    //getting new values from the editText field
//            // Optionally compare with the default values to see if there's a change
//            val originalFirstName = tempUserName.getOrElse(0) { "" }
//            val originalLastName = tempUserName.getOrElse(1) { "" }
//            val originalCountry = tempcountry
////            println(newFirstName+""+newLastName+""+newCountry+"changes detected")
//
////            println(tempuser+""+tempcountry+""+tempUserId+"this is the update button and default values")
//
//            if (newFirstName != originalFirstName ||
//                newLastName != originalLastName ||
//                newCountry != originalCountry) {
//
////                comparing if the default values are not equal to the new values i.e user changed these values
//                println(newFirstName+" "+newLastName+" this is the id:     "+emailid+"             "+newCountry+" changes detected invoking db call")
//                CoroutineScope(Dispatchers.IO).launch {
//
//                    val existinguser=session.from("user").select{filter { eq("user_email",emailid) }}.decodeSingle<UserModel>()
//                    println("User exists${existinguser.user_firstname}")
//
//                    val result=session.from("user").update(
//                        {
//                            set("user_firstname",newFirstName)
//                            set("user_lastname",newLastName)
//                            set("user_country",newCountry)
//                        }
//                    ){
//                        filter {
//                            eq("user_email",emailid)
//                        }
//                    }
////                    println(result)
//                    println(result.data)
//                    withContext(Dispatchers.Main){
//                        Toast.makeText(this@EditProfile,"Profile updated successfully${result.data}",Toast.LENGTH_LONG).show()
////                        println(result.data)
//                        if (result.data?.toString()?.isNotEmpty() == true) {
//                            println("Updated successfully: ${result.data}")
//                        } else {
//                            println("Update failed!")
//                        }
//                        finish()
//                    }
//
////                    println(result.user_firstname+" "+result.user_lastname+" "+result.user_country)
//                }
////                finish()
//                // Make the database update call with the new values
////                updateUserProfile(newFirstName, newLastName, newMobileNo, newCountry)
//            } else {
//                Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
    }

    fun getPreviousValues(){

        CoroutineScope(Dispatchers.IO).launch {
            val user = session.auth.retrieveUserForCurrentSession(updateSession = true)
            currentUser=user.email.toString()
            val result=session.from("user").select{
                filter {
                    eq("user_email",user.email.toString())
                }
            }.decodeSingle<UserModel>()
println("current user while setting default values"+result.user_email)
            prevUserFirstName=result.user_firstname.toString()
            prevUserLastName=result.user_lastname.toString()
            prevUserCountry=result.user_country.toString()
            withContext(Dispatchers.Main){
                val userFirstName=findViewById<EditText>(R.id.first_name)
                val userLastName=findViewById<EditText>(R.id.last_name)
                val userCountry=findViewById<EditText>(R.id.country)

                userFirstName.setText(prevUserFirstName)
                userLastName.setText(prevUserLastName)
                userCountry.setText(prevUserCountry)
                //setting previous values from db
            }
        }
    }
}