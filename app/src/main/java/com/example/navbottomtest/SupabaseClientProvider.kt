package com.example.navbottomtest

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    private  const val SUPABASE_URL = "https://yggurtcbkdwtzggpbygg.supabase.co"
    private  const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlnZ3VydGNia2R3dHpnZ3BieWdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzY1MjM1MDgsImV4cCI6MjA1MjA5OTUwOH0.Ez-lR86iPhKzbmSq8fsS0QoUVMpt5Sn7Uf2EJFOHVLw"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    )
    {
        install(Auth)
        install(Postgrest)
    }

}