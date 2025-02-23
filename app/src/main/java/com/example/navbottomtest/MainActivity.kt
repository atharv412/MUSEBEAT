package com.example.navbottomtest


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jan.supabase.auth.auth

class MainActivity : AppCompatActivity() {

   private val session = SupabaseClientProvider.client.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val menuItems=findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,Home())
            .commit()
        menuItems.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    // Handle Home action
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,Home())
                        .commit()
//                    Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navSearch -> {
                    // Handle Search action
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,Search())
                        .commit()
//                    Toast.makeText(this, "Search Selected", Toast.LENGTH_SHORT).show()
                    true
                }
//                R.id.navPlaylists -> {
//                    // Handle Profile action
//                    Toast.makeText(this, "Profile Selected", Toast.LENGTH_SHORT).show()
//                    true
//                }
                else -> false
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.miniPlayerContainer,MiniPlayer())
            .commit()
    }
}
